package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record DeleteCategoryPlaceCommand(
        MemberAuthInfo memberAuthInfo,
        Long categoryId,
        Long categoryPlaceId
) {
}
