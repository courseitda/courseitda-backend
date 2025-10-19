package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;
import java.util.List;

public record updateCategorySequenceResult(
        List<CategorySequenceResponse> categorySequenceResponses
) {
    public static updateCategorySequenceResult from(final List<Category> categories) {
        return new updateCategorySequenceResult(
                categories.stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }

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
}
