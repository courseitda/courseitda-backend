package courseitda.category.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;

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
}
