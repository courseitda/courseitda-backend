package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;

public record CategoryUpdateCommand(
        String name,
        String color
) {

    public static CategoryUpdateCommand from(final CategoryUpdateRequest request) {
        return new CategoryUpdateCommand(
                request.name(),
                request.color()
        );
    }
}
