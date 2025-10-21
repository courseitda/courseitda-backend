package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

public record FindAllCategoriesResult(
        List<CategoryResult> categoryResults
) {

    public static FindAllCategoriesResult from(final List<Category> categories) {
        return new FindAllCategoriesResult(
                categories.stream()
                        .map(CategoryResult::from)
                        .toList());
    }

    public record CategoryResult(
            Long id,
            String name,
            String color,
            Integer sequence,
            Long representativePlaceId,
            CategoryPlacesResult categoryPlacesResult
    ) {

        public static CategoryResult from(final Category category) {
            return new CategoryResult(
                    category.getId(),
                    category.getName(),
                    category.getColor(),
                    category.getSequence(),
                    category.getRepresentativePlace() != null ? category.getRepresentativePlace().getId() : null,
                    CategoryPlacesResult.of(
                            category.getCategoryPlaces(),
                            category.getRepresentativePlace()
                    )
            );
        }

        public record CategoryPlacesResult(
                List<CategoryPlaceResult> categoryPlaceResults
        ) {

            public static CategoryPlacesResult of(
                    final List<CategoryPlace> categoryPlaces,
                    final CategoryPlace representativePlace
            ) {
                return new CategoryPlacesResult(
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
                    double lat,
                    double lng,
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
                            categoryPlace.getPlace().getLatitude(),
                            categoryPlace.getPlace().getLongitude(),
                            isRepresentative
                    );
                }
            }
        }
    }
}
