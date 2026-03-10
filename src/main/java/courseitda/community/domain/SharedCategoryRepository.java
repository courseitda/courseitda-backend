package courseitda.community.domain;

import java.util.List;
import java.util.Optional;

public interface SharedCategoryRepository {

    SharedCategory save(SharedCategory sharedCategory);

    void delete(SharedCategory sharedCategory);

    Optional<SharedCategory> findById(Long sharedCategoryId);

    Optional<SharedCategory> findByIdIncludingDeleted(Long sharedCategoryId);

    List<SharedCategory> findAllByAuthorId(Long authorId);

    List<SharedCategory> findAllOrderByIdDesc(int limit);

    List<SharedCategory> findAllByIdLessThanOrderByIdDesc(Long cursor, int limit);

    List<SharedCategory> findAllByNameContainingOrderByIdDesc(String keyword, int limit);

    List<SharedCategory> findAllByNameContainingAndIdLessThanOrderByIdDesc(String keyword, Long cursor, int limit);
}
