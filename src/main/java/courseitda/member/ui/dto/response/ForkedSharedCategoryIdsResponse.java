package courseitda.member.ui.dto.response;

import java.util.List;

public record ForkedSharedCategoryIdsResponse(
        List<Long> forkedSharedCategoryIds
) {

    public static ForkedSharedCategoryIdsResponse from(final List<Long> forkedSharedCategoryIds) {
        return new ForkedSharedCategoryIdsResponse(forkedSharedCategoryIds);
    }
}
