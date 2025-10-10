package courseitda.category.ui.dto.response;

import courseitda.category.domain.CategoryPlace;

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
