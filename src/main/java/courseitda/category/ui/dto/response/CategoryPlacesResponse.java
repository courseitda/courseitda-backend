package courseitda.category.ui.dto.response;

import courseitda.category.domain.CategoryPlace;
import java.util.List;
import java.util.Objects;

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
            // todo: 도로명 주소가 있으면 도로명 주소, 없으면 지번 주소
            );
        }
    }
}
