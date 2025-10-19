package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.workspace.application.dto.request.CategoryCreateCommand;
import courseitda.workspace.application.dto.request.CategoryReorderCommand;
import courseitda.workspace.application.dto.request.CategoryUpdateCommand;
import courseitda.workspace.application.dto.response.CreateCategoryResponse;
import courseitda.workspace.application.dto.response.ReadCategoriesResponse;
import courseitda.workspace.application.dto.response.ReadCategoryResponse;
import courseitda.workspace.application.dto.response.ReorderCategoryResponse;
import courseitda.workspace.application.dto.response.UpdateCategoryResponse;
import courseitda.workspace.domain.Category;
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

    private final CategoryRepository categoryRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public CreateCategoryResponse createCategory(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final CategoryCreateCommand command
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        // N+1 문제 해결: workspace.getCategories().size() 대신 직접 count 쿼리 사용
        final var nextSequence = categoryRepository.countByWorkspaceId(workspace.getId()) + 1;
        final var category = Category.createNew(workspace, command.name(), command.color(), nextSequence);
        final var savedCategory = categoryRepository.save(category);

        return CreateCategoryResponse.from(savedCategory);
    }

    @Transactional
    public ReorderCategoryResponse updateCategorySequence(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final CategoryReorderCommand command
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        final var categoryIds = command.categorySequenceRequests().stream()
                .map(CategoryReorderCommand.CategorySequenceCommand::id)
                .toList();

        final var categories = categoryRepository.findAllById(categoryIds);

        // 요청된 카테고리 ID 개수와 실제 조회된 카테고리 개수가 일치하는지 검증
        validateAllCategoriesExist(categories, categoryIds);

        // 순서 변경 요청에 동일 ID가 중복되는지 검증
        validateNoDuplicateCategoryIds(categoryIds);

        // 중복된 sequence 값 검증
        validateNoDuplicateSequences(command);

        for (final var sequenceRequest : command.categorySequenceRequests()) {
            final var category = categories.stream()
                    .filter(c -> c.getId().equals(sequenceRequest.id()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            validateCategoryBelongsToWorkspace(workspace, category);
            category.updateSequence(sequenceRequest.sequence());
        }

        return ReorderCategoryResponse.from(categories);
    }

    @Transactional
    public UpdateCategoryResponse updateCategory(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final Long categoryId,
            final CategoryUpdateCommand command
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());
        validateCategoryBelongsToWorkspace(getWorkspaceByIdentifier(workspaceIdentifier), category);

        category.updateNameAndColor(command.name(), command.color());
        return UpdateCategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());
        validateCategoryBelongsToWorkspace(getWorkspaceByIdentifier(workspaceIdentifier), category);

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public ReadCategoryResponse findCategory(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());
        validateCategoryBelongsToWorkspace(getWorkspaceByIdentifier(workspaceIdentifier), category);

        return ReadCategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public ReadCategoriesResponse findAllCategories(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        final var categories = workspace.getCategories();
        return ReadCategoriesResponse.from(categories);
    }

    private Workspace getWorkspaceByIdentifier(final String workspaceIdentifier) {
        return workspaceRepository.findByIdentifier(workspaceIdentifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORKSPACE_NOT_FOUND));
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

    private void validateNoDuplicateSequences(final CategoryReorderCommand command) {
        final Set<Integer> sequences = new HashSet<>();
        for (final var sequenceRequest : command.categorySequenceRequests()) {
            if (!sequences.add(sequenceRequest.sequence())) {
                throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_ORDER_IN_REQUEST);
            }
        }
    }

    private void validateCategoryBelongsToWorkspace(final Workspace workspace, final Category category) {
        if (!category.getWorkspace().getId().equals(workspace.getId())) {
            throw new BusinessException(ErrorCode.CATEGORY_OUT_OF_WORKSPACE);
        }
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
