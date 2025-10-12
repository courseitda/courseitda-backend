package courseitda.placesearch.domain;

import courseitda.exception.BusinessException;
import courseitda.exception.ErrorCode;

public record SearchedPlace(
        String name,
        String addressName,
        String roadAddressName,
        double latitude,
        double longitude
) {

    public SearchedPlace {
        validateName(name);
        validateAddressName(addressName);
        validateLatitude(latitude);
        validateLongitude(longitude);

        if (roadAddressName.isEmpty()) {
            roadAddressName = null;
        }
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }
    }

    private void validateAddressName(final String addressName) {
        if (addressName == null || addressName.isBlank()) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }
    }

    private void validateLatitude(final double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }
    }

    private void validateLongitude(final double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }
    }
}
