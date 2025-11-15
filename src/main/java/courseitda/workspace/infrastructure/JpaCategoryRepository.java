package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {

    int countByWorkspaceId(Long workspaceId);

    List<Category> findAllByWorkspaceId(Long workspaceId);

    void deleteAllByWorkspaceId(Long workspaceId);
}
