package courseitda.workspace.ui.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.CreateCategoryCommand;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String name,
        @NotBlank String color
) {

    public CreateCategoryCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        return new CreateCategoryCommand(memberAuthInfo, workspaceIdentifier, name, color);
    }
}
