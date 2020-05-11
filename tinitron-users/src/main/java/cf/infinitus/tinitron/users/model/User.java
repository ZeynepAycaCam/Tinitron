package cf.infinitus.tinitron.users.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "accounts")
public class User {

    @Id
    @Column(unique = true)
    protected String id;

    protected String username;

    @NotNull
    @Column(unique = true)
    protected String email;

    @NotNull
    protected String password;

    @NotNull
    protected String role = "user";
}
