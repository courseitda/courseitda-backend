package courseitda.community.infrastructure;

import courseitda.community.domain.RecommendedCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRecommendedCategoryRepository extends JpaRepository<RecommendedCategory, Long> {

    List<RecommendedCategory> findAllByOrderByIdDesc();
}
