package courseitda.workspace.ui.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.UpdateRepresentativeCategoryPlaceCommand;
import jakarta.validation.constraints.NotNull;

public record RepresentativeCategoryPlaceUpdateRequest(
        @NotNull(message = "카테고리 장소 ID는 필수입니다.") Long categoryPlaceId
) {

    public UpdateRepresentativeCategoryPlaceCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        return new UpdateRepresentativeCategoryPlaceCommand(memberAuthInfo, categoryId, categoryPlaceId);
    }
}
