package courseitda.community.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record SharedCategoryCreateRequest(
        @NotNull Long savedCategoryId
) {
}
