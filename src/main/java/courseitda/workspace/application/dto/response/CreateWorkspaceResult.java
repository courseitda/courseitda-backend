package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record CreateWorkspaceResult(
        String identifier,
        String title
) {

    public static CreateWorkspaceResult from(final Workspace workspace) {
        return new CreateWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
