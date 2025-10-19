package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.ReadWorkspaceResult;

public record WorkspaceReadResponse(
        String identifier,
        String title
) {

    public static WorkspaceReadResponse from(final ReadWorkspaceResult response) {
        return new WorkspaceReadResponse(
                response.identifier(),
                response.title()
        );
    }
}
