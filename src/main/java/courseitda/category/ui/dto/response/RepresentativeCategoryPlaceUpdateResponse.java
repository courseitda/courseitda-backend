package courseitda.category.ui.dto.response;

import courseitda.category.domain.CategoryPlace;

public record RepresentativeCategoryPlaceUpdateResponse(
        Long id, // 카테고리 아이디
        Long representativeCategoryPlaceId // 해당 카테고리의 대표 장소 아이디
) {

    public static RepresentativeCategoryPlaceUpdateResponse from(final CategoryPlace categoryPlace) {
        return new RepresentativeCategoryPlaceUpdateResponse(
                categoryPlace.getCategory().getId(),
                categoryPlace.getId()
        );
    }
}
