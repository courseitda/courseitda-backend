package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;

public record CreateCategoryResult(
        Long id,
        String name,
        String color,
        Integer sequence
) {

    public static CreateCategoryResult from(final Category category) {
        return new CreateCategoryResult(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getSequence()
        );
    }
}
