package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.ReadWorkspacesByMemberIdResult;
import courseitda.workspace.application.dto.response.ReadWorkspacesByMemberIdResult.WorkspaceResult;
import java.time.LocalDateTime;
import java.util.List;

public record WorkspacesResponse(
        List<WorkspaceResponse> workspaces
) {

    public static WorkspacesResponse from(final ReadWorkspacesByMemberIdResult result) {
        final List<WorkspaceResponse> workspaceResponses = result.workspaceResults().stream()
                .map(WorkspaceResponse::from)
                .toList();

        return new WorkspacesResponse(workspaceResponses);
    }

    public record WorkspaceResponse(
            String identifier,
            String title,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
    ) {

        public static WorkspaceResponse from(final WorkspaceResult result) {
            return new WorkspaceResponse(
                    result.identifier(),
                    result.title(),
                    result.modifiedAt()
            );
        }
    }
}
