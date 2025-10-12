package courseitda.category.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryReorderRequest;
import courseitda.category.ui.dto.request.CategorySequenceRequest;
import courseitda.category.ui.dto.request.CategoryUpdateRequest;
import courseitda.category.ui.dto.response.CategoriesResponse;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryReorderResponse;
import courseitda.category.ui.dto.response.CategoryResponse;
import courseitda.category.ui.dto.response.CategoryUpdateResponse;
import courseitda.exception.BadRequestException;
import courseitda.exception.ForbiddenException;
import courseitda.exception.NotFoundException;
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
    public CategoryCreateResponse createCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final CategoryCreateRequest request
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        // N+1 문제 해결: workspace.getCategories().size() 대신 직접 count 쿼리 사용
        final var nextSequence = categoryRepository.countByWorkspaceId(workspaceId) + 1;
        final var category = Category.createNew(workspace, request.name(), request.color(), nextSequence);
        final var savedCategory = categoryRepository.save(category);

        return CategoryCreateResponse.from(savedCategory);
    }

    @Transactional
    public CategoryReorderResponse updateCategorySequence(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final CategoryReorderRequest request
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        final var categoryIds = request.categorySequenceRequests().stream()
                .map(CategorySequenceRequest::id)
                .toList();

        final var categories = categoryRepository.findAllById(categoryIds);

        // 요청된 카테고리 ID 개수와 실제 조회된 카테고리 개수가 일치하는지 검증
        validateAllCategoriesExist(categories, categoryIds);

        // 순서 변경 요청에 동일 ID가 중복되는지 검증
        validateNoDuplicateCategoryIds(categoryIds);

        // 중복된 sequence 값 검증
        validateNoDuplicateSequences(request);

        for (final var sequenceRequest : request.categorySequenceRequests()) {
            final var category = categories.stream()
                    .filter(c -> c.getId().equals(sequenceRequest.id()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다."));

            validateCategoryBelongsToWorkspace(workspace, category);
            category.updateSequence(sequenceRequest.sequence());
        }

        return CategoryReorderResponse.from(categories);
    }

    @Transactional
    public CategoryUpdateResponse updateCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final Long categoryId,
            final CategoryUpdateRequest request
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        final var category = getCategoryById(categoryId);
        validateCategoryBelongsToWorkspace(workspace, category);

        category.updateNameAndColor(request.name(), request.color());
        return CategoryUpdateResponse.from(category);
    }

    @Transactional
    public void deleteCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final Long categoryId
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        final var category = getCategoryById(categoryId);
        validateCategoryBelongsToWorkspace(workspace, category);

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final Long categoryId
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        final var category = getCategoryById(categoryId);
        validateCategoryBelongsToWorkspace(workspace, category);

        return CategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public CategoriesResponse findAllCategories(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        final var categories = workspace.getCategories();
        return CategoriesResponse.from(categories);
    }

    private Workspace getWorkspaceById(final Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 워크스페이스를 찾을 수 없습니다."));
    }

    private void validateAllCategoriesExist(final List<Category> categories, final List<Long> categoryIds) {
        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException("일부 카테고리를 찾을 수 없습니다.");
        }
    }

    private void validateNoDuplicateCategoryIds(final List<Long> ids) {
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new BadRequestException("중복된 카테고리 ID가 있습니다.");
        }
    }

    private void validateNoDuplicateSequences(final CategoryReorderRequest request) {
        final Set<Integer> sequences = new HashSet<>();
        for (final var sequenceRequest : request.categorySequenceRequests()) {
            if (!sequences.add(sequenceRequest.sequence())) {
                throw new BadRequestException("중복된 순서 값이 있습니다.");
            }
        }
    }

    private void validateCategoryBelongsToWorkspace(final Workspace workspace, final Category category) {
        if (!category.getWorkspace().getId().equals(workspace.getId())) {
            throw new ForbiddenException("해당 워크스페이스에 속한 카테고리가 아닙니다.");
        }
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다."));
    }
}
