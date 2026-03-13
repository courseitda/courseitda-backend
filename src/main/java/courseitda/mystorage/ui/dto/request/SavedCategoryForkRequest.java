package courseitda.mystorage.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record SavedCategoryForkRequest(
        @NotNull Long sharedCategoryId
) {
}
