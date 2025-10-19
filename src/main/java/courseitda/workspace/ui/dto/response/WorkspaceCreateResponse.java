package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateWorkspaceResponse;

public record WorkspaceCreateResponse(
        String identifier,
        String title
) {

    public static WorkspaceCreateResponse from(final CreateWorkspaceResponse response) {
        return new WorkspaceCreateResponse(
                response.identifier(),
                response.title()
        );
    }
}
