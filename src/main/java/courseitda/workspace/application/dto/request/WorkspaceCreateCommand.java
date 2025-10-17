package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;

public record WorkspaceCreateCommand(
        String title
) {

    public static WorkspaceCreateCommand from(final WorkspaceCreateRequest request) {
        return new WorkspaceCreateCommand(
                request.title()
        );
    }
}
