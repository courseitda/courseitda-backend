package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.ReadCategoryResponse;
import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String color,
        Integer sequence,
        Long representativePlaceId,
        @JsonProperty("categoryPlaces") CategoryPlacesResponse categoryPlacesResponse
) {

    public static CategoryResponse from(final ReadCategoryResponse response) {
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

        public static CategoryPlacesResponse from(final ReadCategoryResponse.CategoryPlacesResponse response) {
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

            public static CategoryPlaceResponse from(final ReadCategoryResponse.CategoryPlacesResponse.CategoryPlaceResponse response) {
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
