package courseitda.category.infrastructure;

import courseitda.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {

    int countByWorkspaceId(Long workspaceId);
}
