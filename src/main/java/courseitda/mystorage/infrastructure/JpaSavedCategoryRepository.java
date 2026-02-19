package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSavedCategoryRepository extends JpaRepository<SavedCategory, Long> {
}
