package courseitda.mystorage.domain;

import java.util.Optional;

public interface SavedCategoryRepository {

    SavedCategory save(SavedCategory savedCategory);

    Optional<SavedCategory> findById(Long savedCategoryId);

    void delete(SavedCategory savedCategory);
}
