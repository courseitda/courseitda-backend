package courseitda.category.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record RepresentativeCategoryPlaceUpdateRequest(
        @NotNull(message = "카테고리 장소 ID는 필수입니다.")
        Long categoryPlaceId
) {
}
