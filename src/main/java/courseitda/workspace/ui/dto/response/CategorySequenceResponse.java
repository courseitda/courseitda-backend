package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.ReorderCategoryResponse;

public record CategorySequenceResponse(
        Long id,
        Integer sequence
) {

    public static CategorySequenceResponse from(final ReorderCategoryResponse.CategorySequenceResponse response) {
        return new CategorySequenceResponse(
                response.id(),
                response.sequence()
        );
    }
}
