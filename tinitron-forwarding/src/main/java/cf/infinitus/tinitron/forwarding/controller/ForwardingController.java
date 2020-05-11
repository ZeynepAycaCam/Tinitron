package cf.infinitus.tinitron.forwarding.controller;

import cf.infinitus.tinitron.forwarding.model.Link;
import cf.infinitus.tinitron.forwarding.service.ForwardingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
public class ForwardingController {

    private final ForwardingService forwardingService;

    public ForwardingController(ForwardingService forwardingService) {
        this.forwardingService = forwardingService;
    }

    @GetMapping("{shortURL}")
    public ResponseEntity<?> forward(@PathVariable String shortURL) {
        Link link = forwardingService.forward(shortURL);

        if (link == null) {
            throw new ResponseStatusException(NOT_FOUND, "The page you’re looking for can’t be found.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(link.getOriginalURL()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

        // TODO: Update stats
        // TODO: Add password checker
    }
}
