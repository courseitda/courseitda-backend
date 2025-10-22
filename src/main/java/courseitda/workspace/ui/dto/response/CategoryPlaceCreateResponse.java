package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryPlaceResult;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId,
        String name,
        String roadAddressName,
        String addressName,
        double latitude,
        double longitude
) {

    public static CategoryPlaceCreateResponse from(final CreateCategoryPlaceResult result) {
        return new CategoryPlaceCreateResponse(
                result.id(),
                result.place().getId(),
                result.place().getName(),
                result.place().getRoadAddressName(),
                result.place().getAddressName(),
                result.place().getLatitude(),
                result.place().getLongitude()
        );
    }
}
