package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;

public record UpdateCategoryResult(
        Long id,
        String name,
        String color
) {

    public static UpdateCategoryResult from(final Category category) {
        return new UpdateCategoryResult(
                category.getId(),
                category.getName(),
                category.getColor()
        );
    }
}
