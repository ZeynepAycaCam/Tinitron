package cf.infinitus.tinitron.link_shortener.service;

import cf.infinitus.tinitron.link_shortener.dao.LinkRepository;
import cf.infinitus.tinitron.link_shortener.model.Link;
import cf.infinitus.tinitron.link_shortener.model.LinkDTO;
import cf.infinitus.tinitron.link_shortener.model.requests.LinkRequest;
import cf.infinitus.tinitron.link_shortener.util.IDConverter;
import cf.infinitus.tinitron.link_shortener.util.URLValidator;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class LinkService {

    private static Long id = 20000000L;
    private final URLValidator urlValidator;
    private final LinkRepository linkRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.urlValidator = new URLValidator();
    }

    public ResponseEntity<?> createShortLink(String uid, LinkRequest request) {
        if (StringUtils.isEmpty(request.getOriginalURL()) &&
                StringUtils.isEmpty(request.getCreationDate()) &&
                StringUtils.isEmpty(request.getExpirationDate())) {
            return new ResponseEntity<>("Missing fields in request.", HttpStatus.BAD_REQUEST);
        }

        if (!urlValidator.validateURL(request.getOriginalURL())) {
            return new ResponseEntity<>("Target URL is not valid.", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getCreationDate());
        calendar.add(Calendar.DATE, 30);

        if (request.getExpirationDate().after(calendar.getTime()) ||
                request.getExpirationDate().before(request.getCreationDate()))
            return new ResponseEntity<>("Invalid expiration date.", HttpStatus.BAD_REQUEST);

        Link newLink = new Link();
        newLink.setTitle(request.getTitle() != null ? request.getTitle() : request.getOriginalURL());
        newLink.setCreationDate(request.getCreationDate());

        newLink.setOriginalURL(request.getOriginalURL());

        if (request.getShortURL() != null) {
            if (linkRepository.findById(request.getShortURL()).isPresent() || request.getShortURL().isEmpty())
                return new ResponseEntity<>("Link already exists.", HttpStatus.CONFLICT);
            newLink.setShortURL(request.getShortURL());
        } else {
            String uniqueID;
            id += linkRepository.count();

            do {
                id += linkRepository.count() % 62;
                uniqueID = IDConverter.createUniqueID(id);
            } while (linkRepository.findById(uniqueID).isPresent());
            newLink.setShortURL(uniqueID);
        }

        newLink.setExpirationDate(request.getExpirationDate());
        newLink.setPassword(request.getPassword());
        newLink.setCreatorId(uid);

        linkRepository.save(newLink);
        return new ResponseEntity<>(new LinkDTO(newLink), HttpStatus.OK);
    }

    public ResponseEntity<?> updateLink(String uid, String shortURL, LinkRequest request) {
        if (StringUtils.isEmpty(request.getTitle()) &&
                StringUtils.isEmpty(request.getShortURL()) &&
                StringUtils.isEmpty(request.getPassword()) &&
                StringUtils.isEmpty(request.getExpirationDate())) {
            return new ResponseEntity<>("Missing fields updating link with key: " + shortURL, HttpStatus.BAD_REQUEST);
        }

        Link existingLink = new Link(linkRepository.findById(shortURL).get());

        if (existingLink != null) {
            if (!existingLink.getCreatorId().equals(uid))
                return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);

            if (request.getTitle() != null)
                existingLink.setTitle(request.getTitle());
            if (request.getPassword() != null)
                existingLink.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getExpirationDate() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(existingLink.getCreationDate());
                calendar.add(Calendar.DATE, 30);

                if (request.getExpirationDate().after(calendar.getTime())) {
                    return new ResponseEntity<>("Expiration date cannot exceed '30' days.", HttpStatus.BAD_REQUEST);
                } else if (request.getExpirationDate().before(existingLink.getCreationDate())) {
                    return new ResponseEntity<>("Expiration date cannot be before creation date.", HttpStatus.BAD_REQUEST);
                }

                existingLink.setExpirationDate(request.getExpirationDate());
            }

            if (request.getShortURL() != null) {
                if (!request.getShortURL().equals(shortURL)) {
                    if (linkRepository.findById(request.getShortURL()).isPresent() || request.getShortURL().isEmpty()) {
                        return new ResponseEntity<>("Short URL is invalid.", HttpStatus.BAD_REQUEST);
                    } else {
                        linkRepository.deleteById(existingLink.getShortURL());
                        existingLink.setShortURL(request.getShortURL());
                    }
                }
            }

            linkRepository.save(existingLink);
            return new ResponseEntity<>(new LinkDTO(existingLink), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No link found with key: " + shortURL, HttpStatus.NOT_FOUND);
        }
    }

    public boolean expireLinks(String[] links) {
        for (String link : links) {
            if (linkRepository.findById(link).isPresent()) {
                var existingLink = linkRepository.findById(link).get();
                existingLink.setExpirationDate(new Date());
                linkRepository.save(existingLink);
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean deleteLinks(String[] links) {
        try {
            for (String link : links) {
                linkRepository.deleteById(link);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Link getShortLink(String shortURL) {
        return linkRepository.findById(shortURL).orElse(null);
    }

    public List<LinkDTO> getAllLinks(Integer pageNo, Integer pageSize, String sortBy) {
        List<LinkDTO> linkDTOList = new ArrayList<>();
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Link> pagedResult = linkRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            for (Link link : pagedResult.getContent()) {
                linkDTOList.add(new LinkDTO(link));
            }
        }
        return linkDTOList;
    }

    public List<LinkDTO> getAllLinksOfUser(String uid, Integer pageNo, Integer pageSize, String sortBy) {
        List<LinkDTO> userLinksDTOList = new ArrayList<>();
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Link> pagedResult = linkRepository.findByCreatorId(uid, paging);

        if (pagedResult.hasContent()) {
            for (Link link : pagedResult.getContent()) {
                userLinksDTOList.add(new LinkDTO(link));
            }
        }
        return userLinksDTOList;
    }

    public boolean validateLinks(String uid, String[] links) {
        for (String link : links) {
            if (!linkRepository.findById(link).isPresent() ||
                    !linkRepository.findById(link).get().getCreatorId().equals(uid))
                return false;
        }
        return true;
    }
}
