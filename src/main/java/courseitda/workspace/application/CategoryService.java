package courseitda.workspace.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.workspace.application.dto.request.CreateCategoryCommand;
import courseitda.workspace.application.dto.request.DeleteCategoryCommand;
import courseitda.workspace.application.dto.request.DeleteRepresentativeCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.FindAllCategoriesCommand;
import courseitda.workspace.application.dto.request.FindCategoryCommand;
import courseitda.workspace.application.dto.request.UpdateCategoryCommand;
import courseitda.workspace.application.dto.request.UpdateCategorySequenceCommand;
import courseitda.workspace.application.dto.request.UpdateRepresentativeCategoryPlaceCommand;
import courseitda.workspace.application.dto.response.CreateCategoryResult;
import courseitda.workspace.application.dto.response.FindCategoriesResult;
import courseitda.workspace.application.dto.response.FindCategoryResult;
import courseitda.workspace.application.dto.response.UpdateCategoryResult;
import courseitda.workspace.application.dto.response.UpdateCategorySequenceResult;
import courseitda.workspace.application.dto.response.UpdateRepresentativeCategoryPlaceResult;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final WorkspaceRepository workspaceRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryPlaceRepository categoryPlaceRepository;

    @Transactional
    public CreateCategoryResult createCategory(
            final CreateCategoryCommand command
    ) {
        final var workspace = getWorkspaceByIdentifier(command.workspaceIdentifier());
        workspace.validateOwnership(command.memberAuthInfo().id());

        // N+1 문제 해결: workspace.getCategories().size() 대신 직접 count 쿼리 사용
        final var nextSequence = categoryRepository.countByWorkspaceId(workspace.getId()) + 1;
        final var category = Category.createNew(workspace, command.name(), command.color(), nextSequence);
        final var savedCategory = categoryRepository.save(category);

        return CreateCategoryResult.from(savedCategory);
    }

    @Transactional
    public UpdateCategoryResult updateCategory(
            final UpdateCategoryCommand command
    ) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        category.updateNameAndColor(command.name(), command.color());
        return UpdateCategoryResult.from(category);
    }

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
    public UpdateCategorySequenceResult updateCategorySequence(
            final UpdateCategorySequenceCommand command
    ) {
        final var workspace = getWorkspaceByIdentifier(command.workspaceIdentifier());
        workspace.validateOwnership(command.memberAuthInfo().id());

        final var categoryIds = command.categorySequenceCommands().stream()
                .map(UpdateCategorySequenceCommand.CategorySequenceCommand::id)
                .toList();

        final var categories = categoryRepository.findAllById(categoryIds);

        // 요청된 카테고리 ID 개수와 실제 조회된 카테고리 개수가 일치하는지 검증
        validateAllCategoriesExist(categories, categoryIds);

        // 순서 변경 요청에 동일 ID가 중복되는지 검증
        validateNoDuplicateCategoryIds(categoryIds);

        // 중복된 sequence 값 검증
        validateNoDuplicateSequences(command);

        for (final var sequenceCommand : command.categorySequenceCommands()) {
            final var category = categories.stream()
                    .filter(c -> c.getId().equals(sequenceCommand.id()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            validateCategoryBelongsToWorkspace(workspace, category);
            category.updateSequence(sequenceCommand.sequence());
        }

        return UpdateCategorySequenceResult.from(categories);
    }

    @Transactional
    public void deleteRepresentativeCategoryPlace(final DeleteRepresentativeCategoryPlaceCommand command) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        category.updateRepresentativePlaceTo(null);
    }

    @Transactional
    public void deleteCategory(
            final DeleteCategoryCommand command
    ) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public FindCategoryResult findCategory(
            final FindCategoryCommand command
    ) {
        final var category = getCategoryById(command.categoryId());
        category.validateOwnership(command.memberAuthInfo().id());

        return FindCategoryResult.from(category);
    }

    @Transactional(readOnly = true)
    public FindCategoriesResult findAllCategories(
            final FindAllCategoriesCommand command
    ) {
        final var workspace = getWorkspaceByIdentifier(command.workspaceIdentifier());
        workspace.validateOwnership(command.memberAuthInfo().id());

        final var categories = workspace.getCategories();
        return FindCategoriesResult.from(categories);
    }

    private void validateAllCategoriesExist(final List<Category> categories, final List<Long> categoryIds) {
        if (categories.size() != categoryIds.size()) {
            throw new BusinessException(ErrorCode.PARTIAL_CATEGORY_NOT_FOUND);
        }
    }

    private void validateNoDuplicateCategoryIds(final List<Long> ids) {
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_ID_IN_REQUEST);
        }
    }

    private void validateNoDuplicateSequences(final UpdateCategorySequenceCommand command) {
        final Set<Integer> sequences = new HashSet<>();
        for (final var sequenceCommand : command.categorySequenceCommands()) {
            if (!sequences.add(sequenceCommand.sequence())) {
                throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_ORDER_IN_REQUEST);
            }
        }
    }

    private void validateCategoryBelongsToWorkspace(final Workspace workspace, final Category category) {
        if (!category.getWorkspace().getId().equals(workspace.getId())) {
            throw new BusinessException(ErrorCode.CATEGORY_OUT_OF_WORKSPACE);
        }
    }

    private Workspace getWorkspaceByIdentifier(final String workspaceIdentifier) {
        return workspaceRepository.findByIdentifier(workspaceIdentifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORKSPACE_NOT_FOUND));
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
