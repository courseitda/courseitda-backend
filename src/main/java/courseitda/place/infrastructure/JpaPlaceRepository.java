package courseitda.place.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.place.domain.Place;

public interface JpaPlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findPlaceByNameAndAddressName(String name, String addressName);
}
