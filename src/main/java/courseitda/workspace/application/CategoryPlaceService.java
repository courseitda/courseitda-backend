package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
import courseitda.workspace.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlacesFindResponse;
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
            final CategoryPlaceCreateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        final var place = findOrCreatePlace(request);

        final var categoryPlace = CategoryPlace.createNew(category, place);
        final var savedCategoryPlace = categoryPlaceRepository.save(categoryPlace);

        return CategoryPlaceCreateResponse.from(savedCategoryPlace);
    }

    @Transactional
    public void deleteCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId,
            final Long categoryPlaceId
    ) {
        final var category = getCategoryById(categoryId);
        final var categoryPlace = getCategoryPlaceById(categoryPlaceId);

        categoryPlace.validateOwnership(memberAuthInfo.id());
        validateCategoryOwnership(categoryId, categoryPlace);

        // 대표 장소인 경우 먼저 해제
        if (category.getRepresentativePlace() != null &&
                category.getRepresentativePlace().getId().equals(categoryPlaceId)) {
            category.updateRepresentativePlaceTo(null);
        }

        categoryPlaceRepository.delete(categoryPlace);
    }

    @Transactional(readOnly = true)
    public CategoryPlacesFindResponse findCategoryPlaces(final MemberAuthInfo memberAuthInfo, final Long categoryId) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        final var categoryPlaces = category.getCategoryPlaces();
        final var representativePlace = category.getRepresentativePlace();

        return CategoryPlacesFindResponse.of(categoryPlaces, representativePlace);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Place findOrCreatePlace(final CategoryPlaceCreateRequest request) {
        return placeRepository.findPlaceByNameAndAddressName(request.name(), request.addressName())
                .orElseGet(() -> {
                    final Place newPlace = Place.createNew(
                            request.name(),
                            request.roadAddressName(),
                            request.addressName(),
                            request.latitude(),
                            request.longitude()
                    );
                    return placeRepository.save(newPlace);
                });
    }

    private CategoryPlace getCategoryPlaceById(final Long categoryPlaceId) {
        return categoryPlaceRepository.findById(categoryPlaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_PLACE_NOT_FOUND));
    }

    private void validateCategoryOwnership(final Long categoryId, final CategoryPlace categoryPlace) {
        if (!categoryPlace.belongsToCategory(categoryId)) {
            throw new BusinessException(ErrorCode.PLACE_NOT_BELONG_TO_CATEGORY);
        }
    }
}
