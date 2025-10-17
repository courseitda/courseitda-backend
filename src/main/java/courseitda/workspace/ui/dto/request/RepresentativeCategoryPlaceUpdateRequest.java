package courseitda.workspace.ui.dto.request;

import courseitda.workspace.application.dto.request.RepresentativeCategoryPlaceUpdateCommand;
import jakarta.validation.constraints.NotNull;

public record RepresentativeCategoryPlaceUpdateRequest(
        @NotNull(message = "카테고리 장소 ID는 필수입니다.") Long categoryPlaceId
) {

    public RepresentativeCategoryPlaceUpdateCommand toCommand() {
        return new RepresentativeCategoryPlaceUpdateCommand(categoryPlaceId);
    }
}
