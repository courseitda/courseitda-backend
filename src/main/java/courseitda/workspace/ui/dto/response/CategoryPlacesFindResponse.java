package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.FindCategoryPlacesResult;
import courseitda.workspace.application.dto.response.FindCategoryPlacesResult.CategoryPlaceResult;
import java.util.List;

public record CategoryPlacesFindResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlacesFindResponse from(final FindCategoryPlacesResult result) {
        return new CategoryPlacesFindResponse(
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

        public static CategoryPlaceResponse from(final CategoryPlaceResult result) {
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
