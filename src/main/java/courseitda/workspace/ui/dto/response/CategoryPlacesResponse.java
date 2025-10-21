package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.FindCategoryPlacesResult;
import courseitda.workspace.application.dto.response.FindCategoryPlacesResult.CategoryPlaceResult;
import java.util.List;

public record CategoryPlacesResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlacesResponse from(final FindCategoryPlacesResult result) {
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
            boolean isRepresentative
    ) {

        public static CategoryPlaceResponse from(final CategoryPlaceResult result) {
            return new CategoryPlaceResponse(
                    result.id(),
                    result.name(),
                    result.address(),
                    result.isRepresentative()
            );
        }
    }
}
