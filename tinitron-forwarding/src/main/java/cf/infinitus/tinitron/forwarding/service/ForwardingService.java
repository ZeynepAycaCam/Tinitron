package cf.infinitus.tinitron.forwarding.service;

import cf.infinitus.tinitron.forwarding.dao.LinkRepository;
import cf.infinitus.tinitron.forwarding.model.Link;
import org.springframework.stereotype.Service;

@Service
public class ForwardingService {

    private final LinkRepository linkRepository;

    public ForwardingService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link forward(String shortURL) {
        return linkRepository.findById(shortURL).orElse(null);
    }
}
