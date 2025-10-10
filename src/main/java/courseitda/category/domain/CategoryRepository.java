package courseitda.category.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long categoryId);

    List<Category> findAllById(Iterable<Long> categoryIds);
}
