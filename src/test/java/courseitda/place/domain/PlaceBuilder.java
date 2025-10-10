package courseitda.place.domain;

public class PlaceBuilder {

    private String name = PlaceFixture.anyName();
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

    public Place build() {
        return Place.createNew(name, roadAddressName, addressName, latitude, longitude);
    }
}
