package courseitda.place.domain;

import courseitda.workspace.domain.Place;

public class PlaceBuilder {

    private String name = PlaceFixture.anyName();
    private String placeUrl = PlaceFixture.anyPlaceUrl();
    private String roadAddressName = PlaceFixture.anyRoadAddressName();
    private String addressName = PlaceFixture.anyAddressName();
    private double latitude = PlaceFixture.anyLatitude();
    private double longitude = PlaceFixture.anyLongitude();

    public PlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public PlaceBuilder roadAddressName(final String roadAddressName) {
        this.roadAddressName = roadAddressName;
        return this;
    }

    public PlaceBuilder addressName(final String addressName) {
        this.addressName = addressName;
        return this;
    }

    public PlaceBuilder latitude(final double latitude) {
        this.latitude = latitude;
        return this;
    }

    public PlaceBuilder longitude(final double longitude) {
        this.longitude = longitude;
        return this;
    }

    public PlaceBuilder placeUrl(final String placeUrl) {
        this.placeUrl = placeUrl;
        return this;
    }

    public Place build() {
        return Place.builder()
                .name(name)
                .roadAddressName(roadAddressName)
                .addressName(addressName)
                .latitude(latitude)
                .longitude(longitude)
                .placeUrl(placeUrl)
                .build();
    }
}
