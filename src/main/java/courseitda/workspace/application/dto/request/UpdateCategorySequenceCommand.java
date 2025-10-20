package courseitda.workspace.application.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import java.util.List;

public record UpdateCategorySequenceCommand(
        MemberAuthInfo memberAuthInfo,
        String workspaceIdentifier,
        List<CategorySequenceCommand> categorySequenceCommands
) {

    public record CategorySequenceCommand(
            Long id,
            Integer sequence
    ) {
    }
}
