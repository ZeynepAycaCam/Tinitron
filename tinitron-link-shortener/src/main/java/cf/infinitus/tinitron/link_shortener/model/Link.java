package cf.infinitus.tinitron.link_shortener.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "links")
public class Link {

    @Id
    @Column(unique = true)
    protected String shortURL;

    @NotNull
    protected String originalURL;

    @NotNull
    protected String creatorId;

    @NotNull
    protected String title;

    @NotNull
    protected Date creationDate;

    @NotNull
    protected Date expirationDate;

    protected String password;

    public Link() {
    }

    public Link(Link link) {
        this.shortURL = link.shortURL;
        this.originalURL = link.originalURL;
        this.creatorId = link.creatorId;
        this.title = link.title;
        this.creationDate = link.creationDate;
        this.expirationDate = link.expirationDate;
        this.password = link.password;
    }
}
