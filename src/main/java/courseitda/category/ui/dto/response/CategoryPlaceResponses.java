package courseitda.category.ui.dto.response;

import java.util.List;

import courseitda.category.domain.CategoryPlace;

public record CategoryPlaceResponses(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlaceResponses from(final List<CategoryPlace> categoryPlaces) {
        return new CategoryPlaceResponses(
                categoryPlaces.stream()
                        .map(CategoryPlaceResponse::from)
                        .toList()
        );
    }
}
