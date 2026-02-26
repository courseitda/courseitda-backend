package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSharedCategoryRepository extends JpaRepository<SharedCategory, Long> {

    List<SharedCategory> findAllByAuthorId(Long authorId);
}
