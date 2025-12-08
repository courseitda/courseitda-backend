package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findPlaceByNameAndAddressName(String name, String addressName);
}
