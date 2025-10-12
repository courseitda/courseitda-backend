package courseitda.category.infrastructure;

import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryPlaceRepositoryImpl implements CategoryPlaceRepository {

    private final JpaCategoryPlaceRepository jpaCategoryPlaceRepository;

    @Override
    public CategoryPlace save(final CategoryPlace categoryPlace) {
        return jpaCategoryPlaceRepository.save(categoryPlace);
    }

    @Override
    public void delete(final CategoryPlace categoryPlace) {
        jpaCategoryPlaceRepository.delete(categoryPlace);
    }

    @Override
    public Optional<CategoryPlace> findById(final Long categoryPlaceId) {
        return jpaCategoryPlaceRepository.findById(categoryPlaceId);
    }
}
