package courseitda.community.ui.dto.response;

import courseitda.community.domain.SharedCategoryLike;

public record SharedCategoryLikeCreateResponse(Long id) {

    public static SharedCategoryLikeCreateResponse from(final SharedCategoryLike sharedCategoryLike) {
        return new SharedCategoryLikeCreateResponse(sharedCategoryLike.getId());
    }
}
