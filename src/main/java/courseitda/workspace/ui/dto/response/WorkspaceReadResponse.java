package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.ReadWorkspaceResponse;

public record WorkspaceReadResponse(
        String identifier,
        String title
) {

    public static WorkspaceReadResponse from(final ReadWorkspaceResponse response) {
        return new WorkspaceReadResponse(
                response.identifier(),
                response.title()
        );
    }
}
