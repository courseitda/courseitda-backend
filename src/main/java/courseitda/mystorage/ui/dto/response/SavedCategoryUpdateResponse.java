package courseitda.mystorage.ui.dto.response;

import courseitda.mystorage.domain.SavedCategory;

public record SavedCategoryUpdateResponse(
        Long id,
        String name
) {

    public static SavedCategoryUpdateResponse from(final SavedCategory savedCategory) {
        return new SavedCategoryUpdateResponse(
                savedCategory.getId(),
                savedCategory.getName()
        );
    }
}
