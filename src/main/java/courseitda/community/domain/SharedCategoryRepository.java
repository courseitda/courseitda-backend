package courseitda.community.domain;

import java.util.Optional;

public interface SharedCategoryRepository {

    SharedCategory save(SharedCategory sharedCategory);

    Optional<SharedCategory> findById(Long sharedCategoryId);

    void delete(SharedCategory sharedCategory);
}
