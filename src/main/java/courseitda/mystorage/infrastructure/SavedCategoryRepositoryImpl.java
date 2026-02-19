package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SavedCategoryRepositoryImpl implements SavedCategoryRepository {

    private final JpaSavedCategoryRepository jpaSavedCategoryRepository;

    @Override
    public SavedCategory save(final SavedCategory savedCategory) {
        return jpaSavedCategoryRepository.save(savedCategory);
    }
}
