package courseitda.category.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import lombok.RequiredArgsConstructor;

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

    @Override
    public List<CategoryPlace> findAllByCategoryId(final Long categoryId) {
        return jpaCategoryPlaceRepository.findAllByCategoryId(categoryId);
    }
}
