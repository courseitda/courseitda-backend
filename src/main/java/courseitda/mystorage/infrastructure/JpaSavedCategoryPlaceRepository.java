package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategoryPlace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSavedCategoryPlaceRepository extends JpaRepository<SavedCategoryPlace, Long> {

    List<SavedCategoryPlace> findAllBySavedCategoryId(Long savedCategoryId);

    void deleteAllByIdIn(List<Long> ids);
}
