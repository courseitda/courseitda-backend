package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record CheckWorkspaceTitleDuplicateCommand(
        MemberAuthInfo memberAuthInfo,
        String title
) {
}
