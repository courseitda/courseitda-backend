package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSharedCategoryRepository extends JpaRepository<SharedCategory, Long> {

    Optional<SharedCategory> findByIdAndDeletedAtIsNull(Long id);

    List<SharedCategory> findAllByAuthorIdAndDeletedAtIsNull(Long authorId);

    List<SharedCategory> findAllByDeletedAtIsNullOrderByIdDesc(Pageable pageable);

    List<SharedCategory> findAllByIdLessThanAndDeletedAtIsNullOrderByIdDesc(Long cursor, Pageable pageable);

    List<SharedCategory> findAllByNameContainingAndDeletedAtIsNullOrderByIdDesc(String keyword, Pageable pageable);

    List<SharedCategory> findAllByNameContainingAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(
            String keyword,
            Long cursor,
            Pageable pageable
    );
}
