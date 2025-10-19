package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryPlaceResult;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId
) {

    public static CategoryPlaceCreateResponse from(final CreateCategoryPlaceResult response) {
        return new CategoryPlaceCreateResponse(
                response.id(),
                response.placeId()
        );
    }
}
