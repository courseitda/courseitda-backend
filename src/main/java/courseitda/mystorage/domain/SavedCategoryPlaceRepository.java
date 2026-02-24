package courseitda.mystorage.domain;

import java.util.List;

public interface SavedCategoryPlaceRepository {

    SavedCategoryPlace save(SavedCategoryPlace savedCategoryPlace);

    List<SavedCategoryPlace> findAllBySavedCategoryId(Long savedCategoryId);

    void deleteAllByIds(List<Long> ids);

    void deleteAllBySavedCategoryId(Long savedCategoryId);
}
