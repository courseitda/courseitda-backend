package courseitda.place.domain;

import courseitda.common.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "places")
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

    private Place(Long id, String name, String roadAddressName, String addressName, double latitude, double longitude,
                  String placeUrl) {
        this.id = id;
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
        return new Place(null, name, roadAddressName, addressName, latitude, longitude, emptyPlaceUrl);
    }
}
