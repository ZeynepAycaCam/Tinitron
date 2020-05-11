package cf.infinitus.tinitron.link_shortener.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties("security")
public class RestSecurityProperties {

    List<String> alloweddomains = new ArrayList<>();
    List<String> allowedheaders = new ArrayList<>();
    List<String> allowedmethods = new ArrayList<>();
    List<String> allowedpublicapis = new ArrayList<>();
}
