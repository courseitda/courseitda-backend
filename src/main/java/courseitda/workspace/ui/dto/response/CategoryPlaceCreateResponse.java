package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryPlaceResult;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId
) {

    public static CategoryPlaceCreateResponse from(final CreateCategoryPlaceResult result) {
        return new CategoryPlaceCreateResponse(
                result.id(),
                result.placeId()
        );
    }
}
