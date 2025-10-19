package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.UpdateCategorySequenceResult.CategorySequenceResult;

public record CategorySequenceResponse(
        Long id,
        Integer sequence
) {

    public static CategorySequenceResponse from(final CategorySequenceResult result) {
        return new CategorySequenceResponse(
                result.id(),
                result.sequence()
        );
    }
}
