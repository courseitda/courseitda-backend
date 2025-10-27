package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record FindWorkspaceCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier
) {
}
