package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateRepresentativeCategoryPlaceResponse;

public record RepresentativeCategoryPlaceUpdateResponse(
        Long id, // 카테고리 아이디
        Long representativeCategoryPlaceId // 해당 카테고리의 대표 장소 아이디
) {

    public static RepresentativeCategoryPlaceUpdateResponse from(final UpdateRepresentativeCategoryPlaceResponse response) {
        return new RepresentativeCategoryPlaceUpdateResponse(
                response.id(),
                response.representativeCategoryPlaceId()
        );
    }
}
