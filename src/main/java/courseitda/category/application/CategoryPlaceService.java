package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
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

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("")); // todo: notfound로 수정
    }

    private Place findOrCreatePlace(final CategoryPlaceCreateRequest request) {
        return placeRepository.findPlaceByNameAndAddressName(request.name(), request.addressName())
                .orElse(
                        placeRepository.save(
                                Place.createNew(
                                        request.name(),
                                        request.roadAddressName(),
                                        request.addressName(),
                                        request.lat(),
                                        request.lng()
                                )
                        ));
    }
}
