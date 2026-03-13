package courseitda.member.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.SharedCategory;
import java.time.LocalDateTime;
import java.util.List;

public record MySharedCategoriesResponse(
        @JsonProperty("sharedCategories") List<SharedCategoryResponse> sharedCategoryResponses,
        boolean hasNext,
        Long nextCursor
) {

    public static MySharedCategoriesResponse from(final List<SharedCategory> sharedCategories, final boolean hasNext, final Long nextCursor) {
        final List<SharedCategoryResponse> sharedCategoryResponses = sharedCategories.stream()
                .map(SharedCategoryResponse::from)
                .toList();

        return new MySharedCategoriesResponse(sharedCategoryResponses, hasNext, nextCursor);
    }

    public record SharedCategoryResponse(
            Long id,
            String name,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime createdAt,
            int placeCount,
            int forkCount
    ) {

        public static SharedCategoryResponse from(final SharedCategory sharedCategory) {
            return new SharedCategoryResponse(
                    sharedCategory.getId(),
                    sharedCategory.getName(),
                    sharedCategory.getCreatedAt(),
                    sharedCategory.getSharedCategoryPlaces().size(),
                    sharedCategory.getForkCount()
            );
        }
    }
}
