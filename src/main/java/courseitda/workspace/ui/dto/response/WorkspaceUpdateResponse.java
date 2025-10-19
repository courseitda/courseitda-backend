package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateWorkspaceResponse;

public record WorkspaceUpdateResponse(
        String identifier,
        String title
) {

    public static WorkspaceUpdateResponse from(final UpdateWorkspaceResponse response) {
        return new WorkspaceUpdateResponse(
                response.identifier(),
                response.title()
        );
    }
}
