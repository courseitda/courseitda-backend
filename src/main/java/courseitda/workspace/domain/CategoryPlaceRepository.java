package courseitda.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryPlaceRepository {

    CategoryPlace save(CategoryPlace categoryPlace);

    void delete(CategoryPlace categoryPlace);

    void deleteAllByCategoryIds(List<Long> categoryIds);

    Optional<CategoryPlace> findById(Long categoryPlaceId);

    List<CategoryPlace> findAllByCategoryIds(List<Long> categoryIds);
}
