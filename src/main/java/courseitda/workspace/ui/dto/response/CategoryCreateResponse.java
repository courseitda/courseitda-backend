package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateCategoryResponse;

public record CategoryCreateResponse(
        Long id,
        String name,
        String color,
        Integer sequence
) {

    public static CategoryCreateResponse from(final CreateCategoryResponse response) {
        return new CategoryCreateResponse(
                response.id(),
                response.name(),
                response.color(),
                response.sequence()
        );
    }
}
