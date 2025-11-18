package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.CategoryPlace;

public record CategoryPlaceCreateResponse(
        Long id,
        Long placeId,
        String name,
        String placeUrl,
        String roadAddressName,
        String addressName,
        double latitude,
        double longitude
) {

    public static CategoryPlaceCreateResponse from(final CategoryPlace categoryPlace) {
        return new CategoryPlaceCreateResponse(
                categoryPlace.getId(),
                categoryPlace.getPlace().getId(),
                categoryPlace.getPlace().getName(),
                categoryPlace.getPlace().getPlaceUrl(),
                categoryPlace.getPlace().getRoadAddressName(),
                categoryPlace.getPlace().getAddressName(),
                categoryPlace.getPlace().getLatitude(),
                categoryPlace.getPlace().getLongitude()
        );
    }
}
