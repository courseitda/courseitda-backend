package courseitda.community.domain;

import java.util.List;

public interface RecommendedCategoryRepository {

    List<RecommendedCategory> findAllOrderByIdDesc();
}
