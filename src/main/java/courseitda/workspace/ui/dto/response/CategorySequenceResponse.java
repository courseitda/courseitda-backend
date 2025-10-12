package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Category;

public record CategorySequenceResponse(
        Long id,
        Integer sequence
) {

    public static CategorySequenceResponse from(final Category category) {
        return new CategorySequenceResponse(
                category.getId(),
                category.getSequence()
        );
    }
}
