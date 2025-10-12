package courseitda.category.ui.dto.response;

import courseitda.category.domain.Category;

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
