package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record ReadWorkspaceCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier
) {
}
