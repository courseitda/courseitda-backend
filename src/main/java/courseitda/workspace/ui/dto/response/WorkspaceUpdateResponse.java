package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateWorkspaceResult;

public record WorkspaceUpdateResponse(
        String identifier,
        String title
) {

    public static WorkspaceUpdateResponse from(final UpdateWorkspaceResult response) {
        return new WorkspaceUpdateResponse(
                response.identifier(),
                response.title()
        );
    }
}
