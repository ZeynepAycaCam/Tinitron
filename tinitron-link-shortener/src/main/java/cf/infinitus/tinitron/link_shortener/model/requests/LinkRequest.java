package cf.infinitus.tinitron.link_shortener.model.requests;

import lombok.Data;

import java.util.Date;

@Data
public class LinkRequest {

    private String title;
    private String shortURL;
    private String originalURL;
    private Date creationDate;
    private Date expirationDate;
    private String password;
}
