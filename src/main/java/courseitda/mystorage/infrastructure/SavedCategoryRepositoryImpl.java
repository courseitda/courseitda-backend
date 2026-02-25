package courseitda.mystorage.infrastructure;

import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<SavedCategory> findById(final Long savedCategoryId) {
        return jpaSavedCategoryRepository.findById(savedCategoryId);
    }

    @Override
    public void delete(final SavedCategory savedCategory) {
        jpaSavedCategoryRepository.delete(savedCategory);
    }

    @Override
    public List<SavedCategory> findAllByOwnerId(Long ownerId) {
        return jpaSavedCategoryRepository.findAllByOwnerId(ownerId);
    }
}
