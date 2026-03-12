package courseitda.mystorage.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SavedCategoryUpdateRequest(
        @NotBlank String name
) {
}
