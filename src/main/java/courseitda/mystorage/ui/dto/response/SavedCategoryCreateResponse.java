package courseitda.mystorage.ui.dto.response;

import courseitda.mystorage.domain.SavedCategory;

public record SavedCategoryCreateResponse(
        Long id,
        String name
) {

    public static SavedCategoryCreateResponse from(final SavedCategory savedCategory) {
        return new SavedCategoryCreateResponse(
                savedCategory.getId(),
                savedCategory.getName()
        );
    }
}
