package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record UpdateWorkspaceCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier,
        String title
) {
}
