package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Category;

public record CategoryCreateResponse(
        Long id,
        String name,
        String color,
        Integer sequence
) {

    public static CategoryCreateResponse from(final Category category) {
        return new CategoryCreateResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getSequence()
        );
    }
}
