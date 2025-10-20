package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record DeleteRepresentativeCategoryPlaceCommand(
        MemberAuthInfo memberAuthInfo,
        Long categoryId
) {
}
