package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSavedCategoryRepository extends JpaRepository<SavedCategory, Long> {

    Optional<SavedCategory> findByIdAndDeletedAtIsNull(Long id);

    List<SavedCategory> findAllByOwnerIdAndDeletedAtIsNull(Long ownerId);

    List<SavedCategory> findAllByOwnerIdAndDeletedAtIsNullOrderByIdDesc(Long ownerId, Pageable pageable);

    List<SavedCategory> findAllByOwnerIdAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(Long ownerId, Long cursor,
            Pageable pageable);
}
