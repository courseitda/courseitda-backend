package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.CategoryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryPlaceRepository extends JpaRepository<CategoryPlace, Long> {
}
