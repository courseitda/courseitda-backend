package courseitda.category.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPlaceRepository extends JpaRepository<CategoryPlace, Long> {
}
