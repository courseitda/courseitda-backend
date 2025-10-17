package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;

public record WorkspaceUpdateCommand(
        String title
) {

    public static WorkspaceUpdateCommand from(final WorkspaceUpdateRequest request) {
        return new WorkspaceUpdateCommand(
                request.title()
        );
    }
}
