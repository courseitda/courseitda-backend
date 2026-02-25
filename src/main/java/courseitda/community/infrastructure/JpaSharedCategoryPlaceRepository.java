package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSharedCategoryPlaceRepository extends JpaRepository<SharedCategoryPlace, Long> {
}
