package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record ReadWorkspaceResult(
        String identifier,
        String title
) {

    public static ReadWorkspaceResult from(final Workspace workspace) {
        return new ReadWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
