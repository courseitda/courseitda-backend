package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;

public record UpdateCategoryResponse(
        Long id,
        String name,
        String color
) {

    public static UpdateCategoryResponse from(final Category category) {
        return new UpdateCategoryResponse(
                category.getId(),
                category.getName(),
                category.getColor()
        );
    }
}
