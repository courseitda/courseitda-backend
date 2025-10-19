package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateCategoryResult;

public record CategoryUpdateResponse(
        Long id,
        String name,
        String color
) {

    public static CategoryUpdateResponse from(final UpdateCategoryResult result) {
        return new CategoryUpdateResponse(
                result.id(),
                result.name(),
                result.color()
        );
    }
}
