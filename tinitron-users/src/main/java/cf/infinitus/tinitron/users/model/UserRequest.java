package cf.infinitus.tinitron.users.model;

import lombok.Data;

@Data
public class UserRequest {

    private String id;
    private String username;
    private String email;
    private String password;
}
