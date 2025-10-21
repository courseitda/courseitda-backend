package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record CreateCategoryCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier,
        String name,
        String color
) {
}
