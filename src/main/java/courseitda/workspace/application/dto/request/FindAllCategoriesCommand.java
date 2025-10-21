package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record FindAllCategoriesCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier
) {
}
