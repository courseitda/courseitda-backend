package courseitda.category.ui.dto.response;

import courseitda.category.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

public record CategoryPlaceResponses(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlaceResponses of(
            final List<CategoryPlace> categoryPlaces,
            final CategoryPlace representativePlace
    ) {
        return new CategoryPlaceResponses(
                categoryPlaces.stream()
                        .map(categoryPlace -> {
                            boolean isRepresentative = representativePlace != null &&
                                    Objects.equals(categoryPlace.getId(), representativePlace.getId());

                            return CategoryPlaceResponse.of(
                                    categoryPlace,
                                    isRepresentative
                            );
                        })
                        .toList()
        );
    }
}
