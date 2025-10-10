package courseitda.category.domain;

import java.util.Optional;

public interface CategoryPlaceRepository {

    CategoryPlace save(CategoryPlace categoryPlace);

    void delete(CategoryPlace categoryPlace);

    Optional<CategoryPlace> findById(Long categoryPlaceId);
}
