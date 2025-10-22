package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record UpdateCategoryCommand(
        MemberAuthInfo memberAuthInfo,
        Long categoryId,
        String name,
        String color
) {
}
