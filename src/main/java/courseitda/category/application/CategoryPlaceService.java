package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.category.ui.dto.response.CategoryPlacesResponse;
import courseitda.exception.ForbiddenException;
import courseitda.exception.NotFoundException;
import courseitda.member.domain.Member;
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
            final Member member,
            final Long categoryId,
            final CategoryPlaceCreateRequest request
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(member);

        final var place = findOrCreatePlace(request);

        final var categoryPlace = CategoryPlace.createNew(category, place);
        final var savedCategoryPlace = categoryPlaceRepository.save(categoryPlace);

        return CategoryPlaceCreateResponse.from(savedCategoryPlace);
    }

    @Transactional
    public void deleteCategoryPlace(final Member member, final Long categoryId, final Long categoryPlaceId) {
        final var category = getCategoryById(categoryId);
        final var categoryPlace = getCategoryPlaceById(categoryPlaceId);

        categoryPlace.validateOwnership(member);
        validateCategoryOwnership(categoryId, categoryPlace);

        // 대표 장소인 경우 먼저 해제
        if (category.getRepresentativePlace() != null &&
                category.getRepresentativePlace().getId().equals(categoryPlaceId)) {
            category.updateRepresentativePlaceTo(null);
        }

        categoryPlaceRepository.delete(categoryPlace);
    }

    @Transactional(readOnly = true)
    public CategoryPlacesResponse findCategoryPlaces(final Member member, final Long categoryId) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(member);

        final var categoryPlaces = category.getCategoryPlaces();
        final var representativePlace = category.getRepresentativePlace();

        return CategoryPlacesResponse.of(categoryPlaces, representativePlace);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다."));
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
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리 장소를 찾을 수 없습니다."));
    }

    private void validateCategoryOwnership(final Long categoryId, final CategoryPlace categoryPlace) {
        if (!categoryPlace.belongsToCategory(categoryId)) {
            throw new ForbiddenException("해당 카테고리에 속한 장소가 아닙니다.");
        }
    }
}
