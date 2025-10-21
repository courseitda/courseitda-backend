package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryResult;

public record CategoryCreateResponse(
        Long id,
        String name,
        String color,
        Integer sequence
) {

    public static CategoryCreateResponse from(final CreateCategoryResult result) {
        return new CategoryCreateResponse(
                result.id(),
                result.name(),
                result.color(),
                result.sequence()
        );
    }
}
