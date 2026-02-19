package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategoryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSavedCategoryPlaceRepository extends JpaRepository<SavedCategoryPlace, Long> {
}
