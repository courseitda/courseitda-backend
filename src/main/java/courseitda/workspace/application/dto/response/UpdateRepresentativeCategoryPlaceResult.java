package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.CategoryPlace;

public record UpdateRepresentativeCategoryPlaceResult(
        Long id, // 카테고리 아이디
        Long representativeCategoryPlaceId // 해당 카테고리의 대표 장소 아이디
) {

    public static UpdateRepresentativeCategoryPlaceResult from(final CategoryPlace categoryPlace) {
        return new UpdateRepresentativeCategoryPlaceResult(
                categoryPlace.getCategory().getId(),
                categoryPlace.getId()
        );
    }
}
