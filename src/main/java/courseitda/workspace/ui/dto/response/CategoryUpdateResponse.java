package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Category;

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
