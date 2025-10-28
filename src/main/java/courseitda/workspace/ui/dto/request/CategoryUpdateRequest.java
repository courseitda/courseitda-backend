package courseitda.workspace.ui.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.UpdateCategoryCommand;
import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank String name,
        @NotBlank String color
) {

    public UpdateCategoryCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        return new UpdateCategoryCommand(memberAuthInfo, categoryId, name, color);
    }
}
