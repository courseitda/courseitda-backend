package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.FindWorkspaceResult;
import java.time.LocalDateTime;

public record WorkspaceFindResponse(
        String identifier,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
) {

    public static WorkspaceFindResponse from(final FindWorkspaceResult result) {
        return new WorkspaceFindResponse(
                result.identifier(),
                result.title(),
                result.modifiedAt()
        );
    }
}
