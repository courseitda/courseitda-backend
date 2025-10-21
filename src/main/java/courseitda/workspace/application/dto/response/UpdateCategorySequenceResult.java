package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;
import java.util.List;

public record UpdateCategorySequenceResult(
        List<CategorySequenceResult> categorySequenceResults
) {
    public static UpdateCategorySequenceResult from(final List<Category> categories) {
        return new UpdateCategorySequenceResult(
                categories.stream()
                        .map(CategorySequenceResult::from)
                        .toList()
        );
    }

    public record CategorySequenceResult(
            Long id,
            Integer sequence
    ) {

        public static CategorySequenceResult from(final Category category) {
            return new CategorySequenceResult(
                    category.getId(),
                    category.getSequence()
            );
        }
    }
}
