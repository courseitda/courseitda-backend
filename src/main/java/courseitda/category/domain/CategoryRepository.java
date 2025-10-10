package courseitda.category.domain;

import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(Long categoryId);
}
