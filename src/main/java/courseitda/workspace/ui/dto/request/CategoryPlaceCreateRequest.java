package courseitda.workspace.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryPlaceCreateRequest(
        @NotBlank String name,
        @NotBlank String placeUrl,
        String roadAddressName,
        @NotBlank String addressName,
        double latitude,
        double longitude
) {
}
