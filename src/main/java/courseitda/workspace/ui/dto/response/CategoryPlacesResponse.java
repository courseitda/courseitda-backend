package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.FindCategoryPlacesResult;
import java.util.List;

public record CategoryPlacesResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlacesResponse from(final FindCategoryPlacesResult response) {
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
            boolean isRepresentative
    ) {

        public static CategoryPlaceResponse from(final FindCategoryPlacesResult.CategoryPlaceResponse response) {
            return new CategoryPlaceResponse(
                    response.id(),
                    response.name(),
                    response.address(),
                    response.isRepresentative()
            );
        }
    }
}
