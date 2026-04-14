package courseitda.community.infrastructure;

import courseitda.community.domain.RecommendedCategory;
import courseitda.community.domain.RecommendedCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendedCategoryRepositoryImpl implements RecommendedCategoryRepository {

    private final JpaRecommendedCategoryRepository jpaRecommendedCategoryRepository;

    @Override
    public List<RecommendedCategory> findAllOrderByIdDesc() {
        return jpaRecommendedCategoryRepository.findAllByOrderByIdDesc();
    }
}
