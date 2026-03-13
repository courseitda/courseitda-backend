package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaSavedCategoryRepository extends JpaRepository<SavedCategory, Long> {

    Optional<SavedCategory> findByIdAndDeletedAtIsNull(Long id);

    List<SavedCategory> findAllByOwnerIdAndDeletedAtIsNull(Long ownerId);

    List<SavedCategory> findAllByOwnerIdAndDeletedAtIsNullOrderByIdDesc(Long ownerId, Pageable pageable);

    List<SavedCategory> findAllByOwnerIdAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(Long ownerId, Long cursor, Pageable pageable);

    @Query("SELECT DISTINCT s.sourceSharedCategoryId FROM SavedCategory s WHERE s.owner.id = :ownerId AND s.sourceSharedCategoryId IN :sharedCategoryIds AND s.deletedAt IS NULL")
    List<Long> findSourceSharedCategoryIdsByOwnerIdAndSourceSharedCategoryIdIn(
            @Param("ownerId") Long ownerId,
            @Param("sharedCategoryIds") List<Long> sharedCategoryIds
    );
}
