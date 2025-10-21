package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;

public record CreateCategoryPlaceCommand(
        MemberAuthInfo memberAuthInfo,
        Long categoryId,
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
) {
}
