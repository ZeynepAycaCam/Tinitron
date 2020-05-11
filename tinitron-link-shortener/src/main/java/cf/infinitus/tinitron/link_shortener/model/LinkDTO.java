package cf.infinitus.tinitron.link_shortener.model;

import lombok.Data;

import java.util.Date;

@Data
public class LinkDTO {

    protected String title;
    protected Date creationDate;

    protected String shortURL;
    protected String originalURL;

    protected Date expirationDate;
    protected String password;

    public LinkDTO(Link link) {
        this.title = link.getTitle();
        this.creationDate = link.getCreationDate();
        this.shortURL = link.getShortURL();
        this.originalURL = link.getOriginalURL();
        this.expirationDate = link.getExpirationDate();
        this.password = link.getPassword();
    }
}
