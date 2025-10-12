package courseitda.place.domain;

import courseitda.common.Timestamp;
import courseitda.exception.BadRequestException;
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

    @Column
    private String roadAddressName;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String placeUrl;

    @Builder
    public Place(
            final String name,
            final String roadAddressName,
            final String addressName,
            final double latitude,
            final double longitude,
            final String placeUrl
    ) {
        validateName(name);
        validateAddressName(addressName);
        validateCoordinates(latitude, longitude);

        this.name = name;
        this.roadAddressName = roadAddressName;
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeUrl = placeUrl;
    }

    public static Place createNew(
            final String name,
            final String roadAddressName,
            final String addressName,
            final double latitude,
            final double longitude
    ) {
        final var emptyPlaceUrl = "";
        return new Place(name, roadAddressName, addressName, latitude, longitude, emptyPlaceUrl);
    }

    private static void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("장소 이름은 필수입니다.");
        }
    }

    private static void validateAddressName(final String addressName) {
        if (addressName == null || addressName.isBlank()) {
            throw new BadRequestException("주소는 필수입니다.");
        }
    }

    private static void validateCoordinates(final double latitude, final double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new BadRequestException("위도는 -90에서 90 사이여야 합니다.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new BadRequestException("경도는 -180에서 180 사이여야 합니다.");
        }
    }
}
