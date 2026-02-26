package courseitda.community.domain;

import java.util.List;
import java.util.Optional;

public interface SharedCategoryRepository {

    SharedCategory save(SharedCategory sharedCategory);

    Optional<SharedCategory> findById(Long sharedCategoryId);

    void delete(SharedCategory sharedCategory);

    List<SharedCategory> findAllByAuthorId(Long authorId);

    List<SharedCategory> findAllOrderByIdDesc(int limit);

    List<SharedCategory> findAllByIdLessThanOrderByIdDesc(Long cursor, int limit);
}
