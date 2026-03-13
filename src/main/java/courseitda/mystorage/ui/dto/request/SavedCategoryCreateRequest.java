package courseitda.mystorage.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SavedCategoryCreateRequest(
        @NotBlank String name
) {
}
