package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSharedCategoryRepository extends JpaRepository<SharedCategory, Long> {

    List<SharedCategory> findAllByAuthorId(Long authorId);

    List<SharedCategory> findAllByOrderByIdDesc(Pageable pageable);

    List<SharedCategory> findAllByIdLessThanOrderByIdDesc(Long cursor, Pageable pageable);

    List<SharedCategory> findAllByNameContainingOrderByIdDesc(String keyword, Pageable pageable);

    List<SharedCategory> findAllByNameContainingAndIdLessThanOrderByIdDesc(String keyword, Long cursor,
            Pageable pageable);
}
