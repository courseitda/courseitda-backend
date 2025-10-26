package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record IsTitleDuplicateCommand(
        MemberAuthInfo memberAuthInfo,
        String title
) {
}
