package courseitda.workspace.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;
import java.util.List;

public record ReadWorkspacesByMemberIdResult(
        List<WorkspaceResult> workspaceResults
) {

    public static ReadWorkspacesByMemberIdResult from(final List<Workspace> workspaces) {
        final List<WorkspaceResult> workspaceResults = workspaces.stream()
                .map(WorkspaceResult::from)
                .toList();

        return new ReadWorkspacesByMemberIdResult(workspaceResults);
    }

    public record WorkspaceResult(
            String identifier,
            String title,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
    ) {

        public static WorkspaceResult from(final Workspace workspace) {
            return new WorkspaceResult(
                    workspace.getIdentifier(),
                    workspace.getTitle(),
                    workspace.getModifiedAt()
            );
        }
    }
}
