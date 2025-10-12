package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.category.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import courseitda.exception.NotFoundException;
import courseitda.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepresentativeCategoryPlaceService {

    private final CategoryRepository categoryRepository;
    private final CategoryPlaceRepository categoryPlaceRepository;

    @Transactional
    public RepresentativeCategoryPlaceUpdateResponse updateRepresentativeCategoryPlace(
            final Member member,
            final Long categoryId,
            final RepresentativeCategoryPlaceUpdateRequest request
    ) {
        var category = getCategoryById(categoryId);
        category.validateOwnership(member);

        var candidatePlace = getCategoryPlaceById(request.categoryPlaceId());

        category.updateRepresentativePlaceTo(candidatePlace);

        return RepresentativeCategoryPlaceUpdateResponse.from(category.getRepresentativePlace());
    }

    @Transactional
    public void deleteRepresentativeCategoryPlace(
            final Member member,
            final Long categoryId
    ) {
        var category = getCategoryById(categoryId);
        category.validateOwnership(member);

        category.updateRepresentativePlaceTo(null);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다."));
    }

    private CategoryPlace getCategoryPlaceById(final Long categoryPlaceId) {
        return categoryPlaceRepository.findById(categoryPlaceId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리 장소를 찾을 수 없습니다."));
    }
}
