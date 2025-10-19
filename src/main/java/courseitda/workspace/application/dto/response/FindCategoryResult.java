package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

public record FindCategoryResult(
        Long id,
        String name,
        String color,
        Integer sequence,
        Long representativePlaceId,
        CategoryPlacesResponse categoryPlacesResponse
) {

    public static FindCategoryResult from(final Category category) {
        return new FindCategoryResult(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getSequence(),
                category.getRepresentativePlace() != null ? category.getRepresentativePlace().getId() : null,
                CategoryPlacesResponse.of(
                        category.getCategoryPlaces(),
                        category.getRepresentativePlace()
                )
        );
    }

    public record CategoryPlacesResponse(
            List<CategoryPlaceResponse> categoryPlaceResponses
    ) {

        public static CategoryPlacesResponse of(
                final List<CategoryPlace> categoryPlaces,
                final CategoryPlace representativePlace
        ) {
            return new CategoryPlacesResponse(
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
                double lat,
                double lng,
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
                        categoryPlace.getPlace().getLatitude(),
                        categoryPlace.getPlace().getLongitude(),
                        isRepresentative
                );
            }
        }
    }
}
