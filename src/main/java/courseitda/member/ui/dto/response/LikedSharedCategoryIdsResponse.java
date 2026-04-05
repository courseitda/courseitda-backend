package courseitda.member.ui.dto.response;

import java.util.List;

public record LikedSharedCategoryIdsResponse(
        List<Long> likedSharedCategoryIds
) {

    public static LikedSharedCategoryIdsResponse from(final List<Long> likedSharedCategoryIds) {
        return new LikedSharedCategoryIdsResponse(likedSharedCategoryIds);
    }
}
