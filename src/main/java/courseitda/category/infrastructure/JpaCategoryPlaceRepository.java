package courseitda.category.infrastructure;

import courseitda.category.domain.CategoryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryPlaceRepository extends JpaRepository<CategoryPlace, Long> {
}
