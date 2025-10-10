package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.category.ui.dto.response.CategoryPlaceResponses;
import courseitda.exception.ForbiddenException;
import courseitda.exception.NotFoundException;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryPlaceService {

    private final CategoryPlaceRepository categoryPlaceRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public CategoryPlaceCreateResponse createCategoryPlace(
            final Long categoryId,
            final CategoryPlaceCreateRequest request
    ) {
        final var category = getCategoryById(categoryId);
        final var place = findOrCreatePlace(request);

        final var categoryPlace = CategoryPlace.createNew(category, place);
        final var savedCategoryPlace = categoryPlaceRepository.save(categoryPlace);

        return CategoryPlaceCreateResponse.from(savedCategoryPlace);
    }

    @Transactional
    public void deleteCategoryPlace(final Long categoryId, final Long categoryPlaceId) {
        final var categoryPlace = getCategoryPlaceById(categoryPlaceId);
        validateCategoryOwnership(categoryId, categoryPlace);
        categoryPlaceRepository.delete(categoryPlace);
    }

    public CategoryPlaceResponses findCategoryPlaces(final Long categoryId) {
        final var categoryPlaces = categoryPlaceRepository.findAllByCategoryId(categoryId);

        final var representativePlace = getCategoryById(categoryId)
                .getRepresentativePlace();

        return CategoryPlaceResponses.of(categoryPlaces, representativePlace);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("카테고리 id에 해당하는 카테고리를 찾을 수 없습니다."));
    }

    private Place findOrCreatePlace(final CategoryPlaceCreateRequest request) {
        return placeRepository.findPlaceByNameAndAddressName(request.name(), request.addressName())
                .orElseGet(() -> {
                    Place newPlace = Place.createNew(
                            request.name(),
                            request.roadAddressName(),
                            request.addressName(),
                            request.lat(),
                            request.lng()
                    );
                    return placeRepository.save(newPlace);
                });
    }

    private CategoryPlace getCategoryPlaceById(final Long categoryPlaceId) {
        return categoryPlaceRepository.findById(categoryPlaceId)
                .orElseThrow(() -> new NotFoundException("카테고리 플레이스 id에 해당하는 카테고리 플레이스를 찾을 수 없습니다."));
    }

    private void validateCategoryOwnership(final Long categoryId, final CategoryPlace categoryPlace) {
        if (!categoryPlace.belongsToCategory(categoryId)) {
            throw new ForbiddenException("해당 카테고리에 속한 장소가 아닙니다.");
        }
    }
}
