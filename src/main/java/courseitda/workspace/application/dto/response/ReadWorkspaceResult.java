package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record ReadWorkspaceResult(
        String identifier,
        String title,
        LocalDateTime modifiedAt
) {

    public static ReadWorkspaceResult from(final Workspace workspace) {
        return new ReadWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getModifiedAt()
        );
    }
}
