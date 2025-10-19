package courseitda.workspace.ui.dto.request;

import courseitda.workspace.application.dto.request.UpdateRepresentativeCategoryPlaceCommand;
import jakarta.validation.constraints.NotNull;

public record RepresentativeCategoryPlaceUpdateRequest(
        @NotNull(message = "카테고리 장소 ID는 필수입니다.") Long categoryPlaceId
) {

    public UpdateRepresentativeCategoryPlaceCommand toCommand() {
        return new UpdateRepresentativeCategoryPlaceCommand(categoryPlaceId);
    }
}
