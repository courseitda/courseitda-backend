package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;

public record RepresentativeCategoryPlaceUpdateCommand(
        Long categoryPlaceId
) {

    public static RepresentativeCategoryPlaceUpdateCommand from(
            final RepresentativeCategoryPlaceUpdateRequest request
    ) {
        return new RepresentativeCategoryPlaceUpdateCommand(
                request.categoryPlaceId()
        );
    }
}
