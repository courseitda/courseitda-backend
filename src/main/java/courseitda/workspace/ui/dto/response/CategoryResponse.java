package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.FindCategoryResult;
import courseitda.workspace.application.dto.response.FindCategoryResult.CategoryPlacesResult;
import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String color,
        Integer sequence,
        Long representativePlaceId,
        @JsonProperty("categoryPlaces") CategoryPlacesResponse categoryPlacesResponse
) {

    public static CategoryResponse from(final FindCategoryResult result) {
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

        public static CategoryPlacesResponse from(final CategoryPlacesResult result) {
            return new CategoryPlacesResponse(
                    result.categoryPlaceResults().stream()
                            .map(CategoryPlaceResponse::from)
                            .toList()
            );
        }

        public record CategoryPlaceResponse(
                Long id,
                String name,
                String address,
                double latitude,
                double longitude,
                boolean isRepresentative
        ) {

            public static CategoryPlaceResponse from(
                    final CategoryPlacesResult.CategoryPlaceResult result) {
                return new CategoryPlaceResponse(
                        result.id(),
                        result.name(),
                        result.address(),
                        result.latitude(),
                        result.longitude(),
                        result.isRepresentative()
                );
            }
        }
    }
}
