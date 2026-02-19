package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
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
}
