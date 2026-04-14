package courseitda.community.application;

import courseitda.community.domain.RecommendedCategoryRepository;
import courseitda.community.ui.dto.response.RecommendedCategoriesReadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendedCategoryService {

    private final RecommendedCategoryRepository recommendedCategoryRepository;

    public RecommendedCategoriesReadResponse findAllRecommendedCategories() {
        final var recommendedCategories = recommendedCategoryRepository.findAllOrderByIdDesc();

        return RecommendedCategoriesReadResponse.from(recommendedCategories);
    }
}
