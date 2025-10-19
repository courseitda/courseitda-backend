package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.CategoryPlace;

public record UpdateRepresentativeCategoryPlaceResponse(
        Long id, // 카테고리 아이디
        Long representativeCategoryPlaceId // 해당 카테고리의 대표 장소 아이디
) {

    public static UpdateRepresentativeCategoryPlaceResponse from(final CategoryPlace categoryPlace) {
        return new UpdateRepresentativeCategoryPlaceResponse(
                categoryPlace.getCategory().getId(),
                categoryPlace.getId()
        );
    }
}
