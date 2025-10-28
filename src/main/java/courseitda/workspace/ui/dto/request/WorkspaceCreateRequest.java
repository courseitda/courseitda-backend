package courseitda.workspace.ui.dto.request;

import courseitda.member.domain.Member;
import courseitda.workspace.application.dto.request.CreateWorkspaceCommand;
import jakarta.validation.constraints.NotBlank;

public record WorkspaceCreateRequest(
        @NotBlank String title
) {

    public CreateWorkspaceCommand toCommandWith(final Member member) {
        return new CreateWorkspaceCommand(member, title);
    }
}
