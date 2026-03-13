package courseitda.member.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.mystorage.domain.SavedCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MySavedCategoriesResponse(
        @JsonProperty("savedCategories") List<SavedCategoryResponse> savedCategoryResponses,
        boolean hasNext,
        Long nextCursor
) {

    public static MySavedCategoriesResponse from(
            final List<SavedCategory> savedCategories,
            final Set<Long> publishableIds,
            final boolean hasNext,
            final Long nextCursor
    ) {
        final List<SavedCategoryResponse> savedCategoryResponses = savedCategories.stream()
                .map(sc -> SavedCategoryResponse.from(sc, publishableIds.contains(sc.getId())))
                .toList();

        return new MySavedCategoriesResponse(savedCategoryResponses, hasNext, nextCursor);
    }

    public record SavedCategoryResponse(
            Long id,
            String name,
            int placeCount,
            Long sourceSharedCategoryId,
            boolean canPublish,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime modifiedAt
    ) {

        public static SavedCategoryResponse from(final SavedCategory savedCategory, final boolean canPublish) {
            return new SavedCategoryResponse(
                    savedCategory.getId(),
                    savedCategory.getName(),
                    savedCategory.getSavedCategoryPlaces().size(),
                    savedCategory.getSourceSharedCategoryId(),
                    canPublish,
                    savedCategory.getModifiedAt()
            );
        }
    }
}
