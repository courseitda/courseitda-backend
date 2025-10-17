package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.CategoryPlaceCreateRequest;

public record CategoryPlaceCreateCommand(
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
) {

    public static CategoryPlaceCreateCommand from(final CategoryPlaceCreateRequest request) {
        return new CategoryPlaceCreateCommand(
                request.name(),
                request.roadAddressName(),
                request.addressName(),
                request.lat(),
                request.lng()
        );
    }
}
