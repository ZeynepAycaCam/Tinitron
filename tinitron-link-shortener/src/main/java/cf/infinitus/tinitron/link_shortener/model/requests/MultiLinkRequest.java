package cf.infinitus.tinitron.link_shortener.model.requests;

import lombok.Data;

@Data
public class MultiLinkRequest {

    private String[] links;
}
