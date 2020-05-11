package cf.infinitus.tinitron.link_shortener.controller;

import cf.infinitus.tinitron.link_shortener.model.Link;
import cf.infinitus.tinitron.link_shortener.model.LinkDTO;
import cf.infinitus.tinitron.link_shortener.model.requests.LinkRequest;
import cf.infinitus.tinitron.link_shortener.model.requests.MultiLinkRequest;
import cf.infinitus.tinitron.link_shortener.security.SecurityUtils;
import cf.infinitus.tinitron.link_shortener.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/links")
public class LinkController {

    @Autowired
    private SecurityUtils securityUtils;
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ResponseEntity<?> getAllLinks(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "creationDate") String sortBy) {

        List<LinkDTO> list = linkService.getAllLinks(pageNo, pageSize, sortBy);
        log.info("Returned page[{}] with pageSize[{}]", pageNo, pageSize);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("users/{uid}")
    public ResponseEntity<?> getAllLinksOfUser(
            @PathVariable String uid,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "creationDate") String sortBy) {

        if (!securityUtils.getPrincipal().getUid().equals(uid)) {
            return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);
        }

        List<LinkDTO> list = linkService.getAllLinksOfUser(uid, pageNo, pageSize, sortBy);
        log.info("Returned user links page[{}] with pageSize[{}]", pageNo, pageSize);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("{shortURL}")
    public ResponseEntity<?> getShortLink(@PathVariable String shortURL) {
        if (!linkService.validateLinks(securityUtils.getPrincipal().getUid(), new String[]{shortURL}))
            return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);

        Link link = linkService.getShortLink(shortURL);

        if (link == null) {
            return new ResponseEntity<>("No link found with key: " + shortURL, HttpStatus.NOT_FOUND);
        }
        log.info("Returned link with key[{}]", shortURL);
        return new ResponseEntity(new LinkDTO(link), HttpStatus.OK);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createShortLink(@RequestBody LinkRequest request) {
        return linkService.createShortLink(securityUtils.getPrincipal().getUid(), request);
    }

    @Transactional
    @PutMapping("{shortURL}")
    public ResponseEntity<?> updateLink(@PathVariable String shortURL, @RequestBody LinkRequest request) {
        return linkService.updateLink(securityUtils.getPrincipal().getUid(), shortURL, request);
    }

    @Transactional
    @PostMapping("expire")
    public ResponseEntity<?> expireLinks(@RequestBody MultiLinkRequest request) {
        if (!linkService.validateLinks(securityUtils.getPrincipal().getUid(), request.getLinks()))
            return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);

        if (linkService.expireLinks(request.getLinks()))
            return new ResponseEntity<>("Links expired", HttpStatus.OK);
        else
            return new ResponseEntity<>("Could not found some links with given keys", HttpStatus.NOT_FOUND);
    }

    @Transactional
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteLinks(@RequestBody MultiLinkRequest request) {
        if (!linkService.validateLinks(securityUtils.getPrincipal().getUid(), request.getLinks()))
            return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);

        if (linkService.deleteLinks(request.getLinks()))
            return new ResponseEntity<>("Links deleted", HttpStatus.OK);
        else
            return new ResponseEntity<>("Could not found some links with given keys", HttpStatus.NOT_FOUND);
    }
}
