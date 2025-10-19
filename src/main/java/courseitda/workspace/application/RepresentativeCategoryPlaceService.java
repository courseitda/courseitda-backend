package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.workspace.application.dto.request.RepresentativeCategoryPlaceUpdateCommand;
import courseitda.workspace.application.dto.response.UpdateRepresentativeCategoryPlaceResponse;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepresentativeCategoryPlaceService {

    private final CategoryRepository categoryRepository;
    private final CategoryPlaceRepository categoryPlaceRepository;

    @Transactional
    public UpdateRepresentativeCategoryPlaceResponse updateRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId,
            final RepresentativeCategoryPlaceUpdateCommand command
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        final var candidatePlace = getCategoryPlaceById(command.categoryPlaceId());

        category.updateRepresentativePlaceTo(candidatePlace);

        return UpdateRepresentativeCategoryPlaceResponse.from(category.getRepresentativePlace());
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
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private CategoryPlace getCategoryPlaceById(final Long categoryPlaceId) {
        return categoryPlaceRepository.findById(categoryPlaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_PLACE_NOT_FOUND));
    }
}
