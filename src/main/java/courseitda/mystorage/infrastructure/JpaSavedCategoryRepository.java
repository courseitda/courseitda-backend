package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSavedCategoryRepository extends JpaRepository<SavedCategory, Long> {

    List<SavedCategory> findAllByOwnerId(Long ownerId);
}
