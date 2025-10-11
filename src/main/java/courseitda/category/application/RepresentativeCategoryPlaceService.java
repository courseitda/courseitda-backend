package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryPlace;
import courseitda.category.domain.CategoryPlaceRepository;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.category.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import courseitda.exception.ForbiddenException;
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
        validateOwnership(member, category);

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
        validateOwnership(member, category);

        category.updateRepresentativePlaceTo(null);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다."));
    }

    private void validateOwnership(final Member member, final Category category) {
        if (!category.isOwnedBy(member)) {
            throw new ForbiddenException("해당 카테고리를 수정할 권한이 없습니다.");
        }
    }

    private CategoryPlace getCategoryPlaceById(final Long categoryPlaceId) {
        return categoryPlaceRepository.findById(categoryPlaceId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리 장소입니다."));
    }
}
