package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.FindAllCategoriesResult;
import courseitda.workspace.application.dto.response.FindAllCategoriesResult.CategoryResult;
import courseitda.workspace.application.dto.response.FindAllCategoriesResult.CategoryResult.CategoryPlacesResult;
import courseitda.workspace.application.dto.response.FindAllCategoriesResult.CategoryResult.CategoryPlacesResult.CategoryPlaceResult;
import java.util.List;

public record CategoriesResponse(
        @JsonProperty("categories") List<CategoryResponse> categoryResponses
) {

    public static CategoriesResponse from(final FindAllCategoriesResult result) {
        return new CategoriesResponse(
                result.categoryResults().stream()
                        .map(CategoryResponse::from)
                        .toList());
    }

    public record CategoryResponse(
            Long id,
            String name,
            String color,
            Integer sequence,
            Long representativePlaceId,
            @JsonProperty("categoryPlaces") CategoryPlacesResponse categoryPlacesResponse
    ) {

        public static CategoryResponse from(final CategoryResult result) {
            return new CategoryResponse(
                    result.id(),
                    result.name(),
                    result.color(),
                    result.sequence(),
                    result.representativePlaceId(),
                    CategoryPlacesResponse.from(result.categoryPlacesResult())
            );
        }

        public record CategoryPlacesResponse(
                @JsonProperty("categoryPlaces") List<CategoryPlaceResponse> categoryPlaceResponses
        ) {

            public static CategoryPlacesResponse from(
                    final CategoryPlacesResult result) {
                return new CategoryPlacesResponse(
                        result.categoryPlaceResults().stream()
                                .map(CategoryPlaceResponse::from)
                                .toList()
                );
            }

            public record CategoryPlaceResponse(
                    Long id,
                    String name,
                    String addressName,
                    String roadAddressName,
                    double latitude,
                    double longitude,
                    boolean isRepresentative
            ) {

                public static CategoryPlaceResponse from(
                        final CategoryPlaceResult result) {
                    return new CategoryPlaceResponse(
                            result.id(),
                            result.name(),
                            result.addressName(),
                            result.roadAddressName(),
                            result.latitude(),
                            result.longitude(),
                            result.isRepresentative()
                    );
                }
            }
        }
    }
}
