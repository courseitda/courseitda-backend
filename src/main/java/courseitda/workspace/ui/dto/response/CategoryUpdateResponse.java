package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateCategoryResponse;

public record CategoryUpdateResponse(
        Long id,
        String name,
        String color
) {

    public static CategoryUpdateResponse from(final UpdateCategoryResponse response) {
        return new CategoryUpdateResponse(
                response.id(),
                response.name(),
                response.color()
        );
    }
}
