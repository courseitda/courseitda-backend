package courseitda.member.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.SharedCategoryLike;
import java.time.LocalDateTime;
import java.util.List;

public record MyLikedSharedCategoriesResponse(
        @JsonProperty("sharedCategories") List<SharedCategoryResponse> sharedCategoryResponses,
        boolean hasNext,
        Long nextCursor
) {

    public static MyLikedSharedCategoriesResponse from(
            final List<SharedCategoryLike> likes,
            final boolean hasNext,
            final Long nextCursor
    ) {
        final List<SharedCategoryResponse> sharedCategoryResponses = likes.stream()
                .map(SharedCategoryResponse::from)
                .toList();

        return new MyLikedSharedCategoriesResponse(sharedCategoryResponses, hasNext, nextCursor);
    }

    public record SharedCategoryResponse(
            Long id,
            String name,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime createdAt,
            int placeCount,
            int likeCount,
            boolean isDeleted
    ) {

        public static SharedCategoryResponse from(final SharedCategoryLike like) {
            final var sharedCategory = like.getSharedCategory();

            return new SharedCategoryResponse(
                    sharedCategory.getId(),
                    sharedCategory.getName(),
                    sharedCategory.getCreatedAt(),
                    sharedCategory.getSharedCategoryPlaces().size(),
                    sharedCategory.getSharedCategoryLikes().size(),
                    sharedCategory.getDeletedAt() != null
            );
        }
    }
}
