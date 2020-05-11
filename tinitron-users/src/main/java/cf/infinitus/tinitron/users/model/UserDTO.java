package cf.infinitus.tinitron.users.model;

import lombok.Data;

@Data
public class UserDTO {

    protected String id;
    protected String username;
    protected String email;
    protected String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
