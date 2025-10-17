package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.CategoryCreateRequest;

public record CategoryCreateCommand(
        String name,
        String color
) {

    public static CategoryCreateCommand from(final CategoryCreateRequest request) {
        return new CategoryCreateCommand(
                request.name(),
                request.color()
        );
    }
}
