package courseitda.place.infrastructure;

import courseitda.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPlaceRepository extends JpaRepository<Place, Long> {
}
