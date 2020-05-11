package cf.infinitus.tinitron.forwarding.model;

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

}
