package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CreateWorkspaceResult;

public record WorkspaceCreateResponse(
        String identifier,
        String title
) {

    public static WorkspaceCreateResponse from(final CreateWorkspaceResult result) {
        return new WorkspaceCreateResponse(
                result.identifier(),
                result.title()
        );
    }
}
