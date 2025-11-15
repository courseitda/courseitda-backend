package courseitda.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long categoryId);

    List<Category> findAllById(Iterable<Long> categoryIds);

    List<Category> findAllByWorkspaceId(Long workspaceId);

    int countByWorkspaceId(Long workspaceId);

    void delete(Category category);

    void deleteAllByWorkspaceId(Long workspaceId);
}
