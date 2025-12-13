package courseitda.placesearch.domain;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;

public record SearchedPlace(
        String name,
        String url,
        String addressName,
        String roadAddressName,
        double latitude,
        double longitude
) {

    public SearchedPlace {
        validateName(name);
        validateUrl(url);
        validateAddressName(addressName);
        validateLatitude(latitude);
        validateLongitude(longitude);

        if (roadAddressName.isEmpty()) {
            roadAddressName = null;
        }
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.SEARCHED_PLACE_NAME_EMPTY);
        }
    }

    private void validateUrl(final String url) {
        if (url == null || url.isBlank()) {
            throw new BusinessException(ErrorCode.SEARCHED_PLACE_URL_EMPTY);
        }
    }

    private void validateAddressName(final String addressName) {
        if (addressName == null || addressName.isBlank()) {
            throw new BusinessException(ErrorCode.SEARCHED_PLACE_ADDRESS_EMPTY);
        }
    }

    private void validateLatitude(final double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new BusinessException(ErrorCode.INVALID_SEARCHED_PLACE_LATITUDE);
        }
    }

    private void validateLongitude(final double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new BusinessException(ErrorCode.INVALID_SEARCHED_PLACE_LONGITUDE);
        }
    }
}
