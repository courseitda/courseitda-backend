package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.NotFoundException;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.workspace.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
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
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId,
            final RepresentativeCategoryPlaceUpdateRequest request
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        final var candidatePlace = getCategoryPlaceById(request.categoryPlaceId());

        category.updateRepresentativePlaceTo(candidatePlace);

        return RepresentativeCategoryPlaceUpdateResponse.from(category.getRepresentativePlace());
    }

    @Transactional
    public void deleteRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

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
