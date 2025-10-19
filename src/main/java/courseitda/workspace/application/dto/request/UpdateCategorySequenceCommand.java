package courseitda.workspace.application.dto.request;

import java.util.List;

public record UpdateCategorySequenceCommand(
        List<CategorySequenceCommand> categorySequenceRequests
) {

    public record CategorySequenceCommand(
            Long id,
            Integer sequence
    ) {
    }
}
