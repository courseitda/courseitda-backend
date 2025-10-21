package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

public record FindCategoryPlacesResult(
        List<CategoryPlaceResult> categoryPlaceResults
) {

    public static FindCategoryPlacesResult of(
            final List<CategoryPlace> categoryPlaces,
            final CategoryPlace representativePlace
    ) {
        return new FindCategoryPlacesResult(
                categoryPlaces.stream()
                        .map(categoryPlace -> {
                            final boolean isRepresentative = representativePlace != null &&
                                    Objects.equals(categoryPlace.getId(), representativePlace.getId());

                            return CategoryPlaceResult.of(
                                    categoryPlace,
                                    isRepresentative
                            );
                        })
                        .toList()
        );
    }

    public record CategoryPlaceResult(
            Long id,
            String name,
            String address,
            boolean isRepresentative
    ) {

        public static CategoryPlaceResult of(
                final CategoryPlace categoryPlace,
                final boolean isRepresentative
        ) {
            return new CategoryPlaceResult(
                    categoryPlace.getId(),
                    categoryPlace.getPlace().getName(),
                    categoryPlace.getPlace().getAddressName(),
                    isRepresentative
            );
        }
    }
}
