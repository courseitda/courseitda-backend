package courseitda.category.ui.dto.response;

import courseitda.category.domain.CategoryPlace;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId
) {

    public static CategoryPlaceCreateResponse from(final CategoryPlace categoryPlace) {
        return new CategoryPlaceCreateResponse(
                categoryPlace.getId(),
                categoryPlace.getPlace().getId()
        );
    }
}
