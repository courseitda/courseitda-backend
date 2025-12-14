package courseitda.place.domain;

import courseitda.common.entity.Timestamp;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "places")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String placeUrl;

    @Column
    private String roadAddressName;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Builder
    public Place(
            final String name,
            final String placeUrl,
            final String roadAddressName,
            final String addressName,
            final double latitude,
            final double longitude
    ) {
        validateName(name);
        validateAddressName(addressName);
        validateCoordinates(latitude, longitude);

        this.name = name;
        this.placeUrl = placeUrl;
        this.roadAddressName = roadAddressName;
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Place createNew(
            final String name,
            final String placeUrl,
            final String roadAddressName,
            final String addressName,
            final double latitude,
            final double longitude
    ) {
        return new Place(name, placeUrl, roadAddressName, addressName, latitude, longitude);
    }

    private static void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.PLACE_NAME_EMPTY);
        }
    }

    private static void validateAddressName(final String addressName) {
        if (addressName == null || addressName.isBlank()) {
            throw new BusinessException(ErrorCode.PLACE_ADDRESS_EMPTY);
        }
    }

    private static void validateCoordinates(final double latitude, final double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new BusinessException(ErrorCode.INVALID_LATITUDE_RANGE);
        }
        if (longitude < -180 || longitude > 180) {
            throw new BusinessException(ErrorCode.INVALID_LONGITUDE_RANGE);
        }
    }
}
