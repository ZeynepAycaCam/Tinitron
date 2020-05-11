package cf.infinitus.tinitron.forwarding.dao;

import cf.infinitus.tinitron.forwarding.model.Link;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LinkRepository extends PagingAndSortingRepository<Link, String> {
}
