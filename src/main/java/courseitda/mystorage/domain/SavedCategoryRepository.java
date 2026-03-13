package courseitda.mystorage.domain;

import java.util.List;
import java.util.Optional;

public interface SavedCategoryRepository {

    SavedCategory save(SavedCategory savedCategory);

    void delete(SavedCategory savedCategory);

    Optional<SavedCategory> findById(Long savedCategoryId);

    List<SavedCategory> findAllByOwnerId(Long ownerId);

    List<SavedCategory> findAllByOwnerIdOrderByIdDesc(Long ownerId, int limit);

    List<SavedCategory> findAllByOwnerIdAndIdLessThanOrderByIdDesc(Long ownerId, Long cursor, int limit);

    List<Long> findAllSourceSharedCategoryIdsByOwnerIdAndSourceSharedCategoryIdIn(Long ownerId,
            List<Long> sharedCategoryIds);
}
