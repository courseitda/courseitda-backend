package courseitda.category.ui.dto.response;

import courseitda.category.domain.Category;

public record CategoryUpdateResponse(
        Long id,
        String name,
        String color
) {

    public static CategoryUpdateResponse from(final Category category) {
        return new CategoryUpdateResponse(
                category.getId(),
                category.getName(),
                category.getColor()
        );
    }
}
