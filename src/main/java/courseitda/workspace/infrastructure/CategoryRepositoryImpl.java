package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public Category save(final Category category) {
        return jpaCategoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(final Long categoryId) {
        return jpaCategoryRepository.findById(categoryId);
    }

    @Override
    public List<Category> findAllById(final Iterable<Long> categoryIds) {
        return jpaCategoryRepository.findAllById(categoryIds);
    }

    @Override
    public List<Category> findAllByWorkspaceId(final Long workspaceId) {
        return jpaCategoryRepository.findAllByWorkspaceId(workspaceId);
    }

    @Override
    public int countByWorkspaceId(final Long workspaceId) {
        return jpaCategoryRepository.countByWorkspaceId(workspaceId);
    }

    @Override
    public void delete(final Category category) {
        jpaCategoryRepository.delete(category);
    }

    @Override
    public void deleteAllByWorkspaceId(final Long workspaceId) {
        jpaCategoryRepository.deleteAllByWorkspaceId(workspaceId);
    }
}
