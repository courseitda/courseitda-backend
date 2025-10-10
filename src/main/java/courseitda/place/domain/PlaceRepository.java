package courseitda.place.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findPlaceByNameAndAddressName(String name, String addressName);
}
