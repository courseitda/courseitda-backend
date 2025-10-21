package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record DeleteCategoryCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier,
        Long categoryId
) {
}
