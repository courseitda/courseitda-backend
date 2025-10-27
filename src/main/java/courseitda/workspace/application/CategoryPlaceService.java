package courseitda.workspace.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import courseitda.workspace.application.dto.request.CreateCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.DeleteCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.FindCategoryPlacesCommand;
import courseitda.workspace.application.dto.response.CreateCategoryPlaceResult;
import courseitda.workspace.application.dto.response.FindCategoryPlacesResult;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
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
    public CreateCategoryPlaceResult createCategoryPlace(final CreateCategoryPlaceCommand command) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        final var place = findOrCreatePlace(command);

        final var categoryPlace = CategoryPlace.createNew(category, place);
        final var savedCategoryPlace = categoryPlaceRepository.save(categoryPlace);

        return CreateCategoryPlaceResult.from(savedCategoryPlace);
    }

    @Transactional
    public void deleteCategoryPlace(final DeleteCategoryPlaceCommand command) {
        final var category = getCategoryById(command.categoryId());
        final var categoryPlace = getCategoryPlaceById(command.categoryPlaceId());

        categoryPlace.validateOwnership(command.memberAuthInfo().id());
        validateCategoryOwnership(command.categoryId(), categoryPlace);

        // 대표 장소인 경우 먼저 해제
        if (category.getRepresentativePlace() != null &&
                category.getRepresentativePlace().getId().equals(command.categoryPlaceId())) {
            category.updateRepresentativePlaceTo(null);
        }

        categoryPlaceRepository.delete(categoryPlace);
    }

    @Transactional(readOnly = true)
    public FindCategoryPlacesResult findCategoryPlaces(final FindCategoryPlacesCommand command) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        final var categoryPlaces = category.getCategoryPlaces();
        final var representativePlace = category.getRepresentativePlace();

        return FindCategoryPlacesResult.of(categoryPlaces, representativePlace);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Place findOrCreatePlace(final CreateCategoryPlaceCommand command) {
        return placeRepository.findPlaceByNameAndAddressName(command.name(), command.addressName())
                .orElseGet(() -> {
                    final Place newPlace = Place.createNew(
                            command.name(),
                            command.roadAddressName(),
                            command.addressName(),
                            command.latitude(),
                            command.longitude()
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
