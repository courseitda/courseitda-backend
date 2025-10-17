package courseitda.workspace.application.dto.request;

import courseitda.workspace.ui.dto.request.CategoryReorderRequest;
import courseitda.workspace.ui.dto.request.CategorySequenceRequest;
import java.util.List;

public record CategoryReorderCommand(
        List<CategorySequenceCommand> categorySequenceRequests
) {

    public static CategoryReorderCommand from(final CategoryReorderRequest request) {
        final List<CategorySequenceCommand> serviceRequests = request.categorySequenceRequests().stream()
                .map(CategorySequenceCommand::from)
                .toList();

        return new CategoryReorderCommand(serviceRequests);
    }

    public record CategorySequenceCommand(
            Long id,
            Integer sequence
    ) {
        public static CategorySequenceCommand from(final CategorySequenceRequest request) {
            return new CategorySequenceCommand(
                    request.id(),
                    request.sequence()
            );
        }
    }
}
