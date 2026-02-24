package courseitda.mystorage.ui.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SavedCategoryUpdateRequest(
        @NotBlank String name,
        @NotEmpty @Valid List<SavedCategoryPlaceRequest> savedCategoryPlaces
) {

    public record SavedCategoryPlaceRequest(
            Long savedCategoryPlaceId, // nullable
            @NotBlank String name,
            @NotBlank String placeUrl,
            String roadAddressName,
            @NotBlank String addressName,
            double latitude,
            double longitude
    ) {
    }
}
