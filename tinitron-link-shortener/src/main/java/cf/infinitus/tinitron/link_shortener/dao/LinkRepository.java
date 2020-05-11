package cf.infinitus.tinitron.link_shortener.dao;

import cf.infinitus.tinitron.link_shortener.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LinkRepository extends PagingAndSortingRepository<Link, String> {

    /**
     * Finds links belonging to a user
     * @param creatorId
     * @param paging
     * @return links of the users with paging
     */
    Page<Link> findByCreatorId(String creatorId, Pageable paging);
}
