package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record WorkspaceCreateResponse(
        String identifier,
        String title,
        @JsonProperty("modifiedAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime lastActivityAt
) {

    public static WorkspaceCreateResponse from(final Workspace workspace) {
        return new WorkspaceCreateResponse(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getLastActivityAt()
        );
    }
}
