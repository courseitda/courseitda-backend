package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPlaceRepository extends JpaRepository<Place, Long> {
}
