package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record WorkspaceUpdateResponse(
        String identifier,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
) {

    public static WorkspaceUpdateResponse from(final Workspace workspace) {
        return new WorkspaceUpdateResponse(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getModifiedAt()
        );
    }
}
