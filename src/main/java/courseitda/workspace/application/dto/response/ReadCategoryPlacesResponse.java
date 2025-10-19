package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

public record ReadCategoryPlacesResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static ReadCategoryPlacesResponse of(
            final List<CategoryPlace> categoryPlaces,
            final CategoryPlace representativePlace
    ) {
        return new ReadCategoryPlacesResponse(
                categoryPlaces.stream()
                        .map(categoryPlace -> {
                            final boolean isRepresentative = representativePlace != null &&
                                    Objects.equals(categoryPlace.getId(), representativePlace.getId());

                            return CategoryPlaceResponse.of(
                                    categoryPlace,
                                    isRepresentative
                            );
                        })
                        .toList()
        );
    }

    public record CategoryPlaceResponse(
            Long id,
            String name,
            String address,
            boolean isRepresentative
    ) {

        public static CategoryPlaceResponse of(
                final CategoryPlace categoryPlace,
                final boolean isRepresentative
        ) {
            return new CategoryPlaceResponse(
                    categoryPlace.getId(),
                    categoryPlace.getPlace().getName(),
                    categoryPlace.getPlace().getAddressName(),
                    isRepresentative
            );
        }
    }
}
