package cf.infinitus.tinitron.users.dao;

import cf.infinitus.tinitron.users.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    /**
     * Find a user with the specified username.
     *
     * @param username
     * @return The user if found, null otherwise.
     */
    User findByUsername(String username);

    /**
     * Find a user with the specified email.
     *
     * @param email
     * @return The user if found, null otherwise.
     */
    User findByEmail(String email);
}
