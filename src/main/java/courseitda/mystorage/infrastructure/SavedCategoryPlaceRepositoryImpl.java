package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SavedCategoryPlaceRepositoryImpl implements SavedCategoryPlaceRepository {

    private final JpaSavedCategoryPlaceRepository jpaSavedCategoryPlaceRepository;

    @Override
    public SavedCategoryPlace save(final SavedCategoryPlace savedCategoryPlace) {
        return jpaSavedCategoryPlaceRepository.save(savedCategoryPlace);
    }

    @Override
    public List<SavedCategoryPlace> findAllBySavedCategoryId(final Long savedCategoryId) {
        return jpaSavedCategoryPlaceRepository.findAllBySavedCategoryId(savedCategoryId);
    }

    @Override
    public void deleteAllByIds(final List<Long> ids) {
        if (!ids.isEmpty()) {
            jpaSavedCategoryPlaceRepository.deleteAllByIdIn(ids);
        }
    }

    @Override
    public void deleteAllBySavedCategoryId(final Long savedCategoryId) {
        jpaSavedCategoryPlaceRepository.deleteAllBySavedCategoryId(savedCategoryId);
    }
}
