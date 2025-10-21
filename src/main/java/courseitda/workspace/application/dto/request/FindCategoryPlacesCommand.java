package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record FindCategoryPlacesCommand(
        MemberAuthInfo memberAuthInfo,
        Long categoryId
) {
}
