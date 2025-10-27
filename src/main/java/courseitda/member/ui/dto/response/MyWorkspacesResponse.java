package courseitda.member.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.FindWorkspacesByMemberIdResult;
import courseitda.workspace.application.dto.response.FindWorkspacesByMemberIdResult.WorkspaceResult;
import java.time.LocalDateTime;
import java.util.List;

public record MyWorkspacesResponse(
        List<WorkspaceResponse> workspaces
) {

    public static MyWorkspacesResponse from(final FindWorkspacesByMemberIdResult result) {
        final List<WorkspaceResponse> workspaceResponses = result.workspaceResults().stream()
                .map(WorkspaceResponse::from)
                .toList();

        return new MyWorkspacesResponse(workspaceResponses);
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
