package courseitda.category.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryPlaceRepository {

    CategoryPlace save(CategoryPlace categoryPlace);

    void delete(CategoryPlace categoryPlace);

    Optional<CategoryPlace> findById(Long categoryPlaceId);

    List<CategoryPlace> findAllByCategoryId(Long categoryId);
}
