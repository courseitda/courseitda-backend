package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;

public record CreateCategoryResponse(
        Long id,
        String name,
        String color,
        Integer sequence
) {

    public static CreateCategoryResponse from(final Category category) {
        return new CreateCategoryResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getSequence()
        );
    }
}
