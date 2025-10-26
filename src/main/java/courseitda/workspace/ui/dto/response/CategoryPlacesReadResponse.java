package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.FindCategoryPlacesResult;
import courseitda.workspace.application.dto.response.FindCategoryPlacesResult.CategoryPlaceResult;
import java.util.List;

public record CategoryPlacesReadResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlacesReadResponse from(final FindCategoryPlacesResult result) {
        return new CategoryPlacesReadResponse(
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
            boolean isRepresentative
    ) {

        public static CategoryPlaceResponse from(final CategoryPlaceResult result) {
            return new CategoryPlaceResponse(
                    result.id(),
                    result.name(),
                    result.addressName(),
                    result.roadAddressName(),
                    result.isRepresentative()
            );
        }
    }
}
