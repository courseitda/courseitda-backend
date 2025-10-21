package courseitda.workspace.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.workspace.application.dto.request.DeleteRepresentativeCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.UpdateRepresentativeCategoryPlaceCommand;
import courseitda.workspace.application.dto.response.UpdateRepresentativeCategoryPlaceResult;
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
    public UpdateRepresentativeCategoryPlaceResult updateRepresentativeCategoryPlace(
            final UpdateRepresentativeCategoryPlaceCommand command
    ) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        final var candidatePlace = getCategoryPlaceById(command.categoryPlaceId());

        category.updateRepresentativePlaceTo(candidatePlace);

        return UpdateRepresentativeCategoryPlaceResult.from(category.getRepresentativePlace());
    }

    @Transactional
    public void deleteRepresentativeCategoryPlace(final DeleteRepresentativeCategoryPlaceCommand command) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

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
