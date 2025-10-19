package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.CategoryPlace;

public record CreateCategoryPlaceResponse(
        Long id,
        Long placeId
) {

    public static CreateCategoryPlaceResponse from(final CategoryPlace categoryPlace) {
        return new CreateCategoryPlaceResponse(
                categoryPlace.getId(),
                categoryPlace.getPlace().getId()
        );
    }
}
