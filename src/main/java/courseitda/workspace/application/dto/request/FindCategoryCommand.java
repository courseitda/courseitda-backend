package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record FindCategoryCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier,
        Long categoryId
) {
}
