package courseitda.category.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리 이름은 필수입니다") @Size(max = 10, message = "카테고리 이름은 10자를 초과할 수 없습니다") String name,

        @NotBlank(message = "색상은 필수입니다") @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "올바른 색상 형식이 아닙니다") String color
) {
}
