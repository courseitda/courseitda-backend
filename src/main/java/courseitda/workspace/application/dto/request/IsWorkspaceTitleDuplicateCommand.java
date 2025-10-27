package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record IsWorkspaceTitleDuplicateCommand(
        MemberAuthInfo memberAuthInfo,
        String title
) {
}
