package courseitda.place.domain;

import java.util.Optional;

public interface PlaceRepository {

    Place save(Place place);

    Optional<Place> findPlaceByNameAndAddressName(String name, String addressName);
}
