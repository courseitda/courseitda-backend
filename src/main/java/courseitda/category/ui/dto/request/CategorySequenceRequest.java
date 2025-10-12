package courseitda.category.ui.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CategorySequenceRequest(
        @NotNull(message = "카테고리 ID는 필수입니다") Long id,

        @NotNull(message = "순서는 필수입니다") @Min(value = 1, message = "순서는 1 이상이어야 합니다") Integer sequence
) {
}
