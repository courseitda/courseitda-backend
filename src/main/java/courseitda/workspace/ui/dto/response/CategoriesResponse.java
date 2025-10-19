package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.FindAllCategoriesResult;
import java.util.List;

public record CategoriesResponse(
        @JsonProperty("categories") List<CategoryResponse> categoryResponses
) {

    public static CategoriesResponse from(final FindAllCategoriesResult response) {
        return new CategoriesResponse(
                response.categoryResponses().stream()
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

        public static CategoryResponse from(final FindAllCategoriesResult.CategoryResponse response) {
            return new CategoryResponse(
                    response.id(),
                    response.name(),
                    response.color(),
                    response.sequence(),
                    response.representativePlaceId(),
                    CategoryPlacesResponse.from(response.categoryPlacesResponse())
            );
        }

        public record CategoryPlacesResponse(
                @JsonProperty("categoryPlaces") List<CategoryPlaceResponse> categoryPlaceResponses
        ) {

            public static CategoryPlacesResponse from(
                    final FindAllCategoriesResult.CategoryResponse.CategoryPlacesResponse response) {
                return new CategoryPlacesResponse(
                        response.categoryPlaceResponses().stream()
                                .map(CategoryPlaceResponse::from)
                                .toList()
                );
            }

            public record CategoryPlaceResponse(
                    Long id,
                    String name,
                    String address,
                    double lat,
                    double lng,
                    boolean isRepresentative
            ) {

                public static CategoryPlaceResponse from(
                        final FindAllCategoriesResult.CategoryResponse.CategoryPlacesResponse.CategoryPlaceResponse response) {
                    return new CategoryPlaceResponse(
                            response.id(),
                            response.name(),
                            response.address(),
                            response.lat(),
                            response.lng(),
                            response.isRepresentative()
                    );
                }
            }
        }
    }
}
