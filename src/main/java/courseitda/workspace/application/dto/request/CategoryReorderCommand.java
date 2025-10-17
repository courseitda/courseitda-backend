package courseitda.workspace.application.dto.request;

import java.util.List;

public record CategoryReorderCommand(
        List<CategorySequenceCommand> categorySequenceRequests
) {

    public record CategorySequenceCommand(
            Long id,
            Integer sequence
    ) {
    }
}
