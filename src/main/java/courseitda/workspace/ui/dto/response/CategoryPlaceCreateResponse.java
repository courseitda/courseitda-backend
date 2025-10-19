package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryPlaceResponse;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId
) {

    public static CategoryPlaceCreateResponse from(final CreateCategoryPlaceResponse response) {
        return new CategoryPlaceCreateResponse(
                response.id(),
                response.placeId()
        );
    }
}
