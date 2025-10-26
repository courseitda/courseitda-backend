package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.CreateWorkspaceResult;
import java.time.LocalDateTime;

public record WorkspaceCreateResponse(
        String identifier,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
) {

    public static WorkspaceCreateResponse from(final CreateWorkspaceResult result) {
        return new WorkspaceCreateResponse(
                result.identifier(),
                result.title(),
                result.modifiedAt()
        );
    }
}
