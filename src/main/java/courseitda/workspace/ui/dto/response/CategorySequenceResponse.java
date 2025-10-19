package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.updateCategorySequenceResult;

public record CategorySequenceResponse(
        Long id,
        Integer sequence
) {

    public static CategorySequenceResponse from(final updateCategorySequenceResult.CategorySequenceResponse response) {
        return new CategorySequenceResponse(
                response.id(),
                response.sequence()
        );
    }
}
