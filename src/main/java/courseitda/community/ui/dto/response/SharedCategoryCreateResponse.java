package courseitda.community.ui.dto.response;

import courseitda.community.domain.SharedCategory;

public record SharedCategoryCreateResponse(
        Long id,
        String name
) {

    public static SharedCategoryCreateResponse from(final SharedCategory sharedCategory) {
        return new SharedCategoryCreateResponse(sharedCategory.getId(), sharedCategory.getName());
    }
}
