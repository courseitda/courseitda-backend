package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record DeleteWorkspaceCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier
) {
}
