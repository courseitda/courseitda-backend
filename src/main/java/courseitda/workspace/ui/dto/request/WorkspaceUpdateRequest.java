package courseitda.workspace.ui.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.UpdateWorkspaceCommand;
import jakarta.validation.constraints.NotBlank;

public record WorkspaceUpdateRequest(
        @NotBlank String title
) {

    public UpdateWorkspaceCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        return new UpdateWorkspaceCommand(memberAuthInfo, workspaceIdentifier, title);
    }
}
