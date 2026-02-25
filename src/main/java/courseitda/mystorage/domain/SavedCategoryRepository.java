package courseitda.mystorage.domain;

import java.util.List;
import java.util.Optional;

public interface SavedCategoryRepository {

    SavedCategory save(SavedCategory savedCategory);

    Optional<SavedCategory> findById(Long savedCategoryId);

    void delete(SavedCategory savedCategory);

    List<SavedCategory> findAllByOwnerId(Long ownerId);
}
