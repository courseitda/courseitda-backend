package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.place.domain.PlaceRepository;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import courseitda.workspace.domain.CategoryPlaceRepository;
import courseitda.workspace.domain.CategoryRepository;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import courseitda.workspace.ui.dto.request.CategoryCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryReorderRequest;
import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;
import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.workspace.ui.dto.response.CategoriesReadResponse;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryReadResponse;
import courseitda.workspace.ui.dto.response.CategorySequenceUpdateResponse;
import courseitda.workspace.ui.dto.response.CategoryUpdateResponse;
import courseitda.workspace.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
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
    private final PlaceRepository placeRepository;

    @Transactional
    public CategoryCreateResponse createCategory(
            final CategoryCreateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        // N+1 문제 해결: workspace.getCategories().size() 대신 직접 count 쿼리 사용
        final var nextSequence = categoryRepository.countByWorkspaceId(workspace.getId()) + 1;
        final var category = Category.createNew(workspace, request.name(), request.color(), nextSequence);
        final var savedCategory = categoryRepository.save(category);
        savedCategory.updateWorkspaceLastActivityAt();

        return CategoryCreateResponse.from(savedCategory);
    }

    @Transactional
    public CategoryUpdateResponse updateCategory(
            final CategoryUpdateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        category.updateNameAndColor(request.name(), request.color());
        category.updateWorkspaceLastActivityAt();

        return CategoryUpdateResponse.from(category);
    }

    @Transactional
    public RepresentativeCategoryPlaceUpdateResponse updateRepresentativeCategoryPlace(
            final RepresentativeCategoryPlaceUpdateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        final var candidatePlace = getCategoryPlaceById(request.categoryPlaceId());

        category.updateRepresentativePlaceTo(candidatePlace);
        category.updateWorkspaceLastActivityAt();

        return RepresentativeCategoryPlaceUpdateResponse.from(category.getRepresentativePlace());
    }

    @Transactional
    public CategorySequenceUpdateResponse updateCategorySequence(
            final CategoryReorderRequest request,
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        final var categoryIds = request.categories().stream()
                .map(CategoryReorderRequest.CategorySequenceRequest::id)
                .toList();

        final var categories = categoryRepository.findAllById(categoryIds);

        // 요청된 카테고리 ID 개수와 실제 조회된 카테고리 개수가 일치하는지 검증
        validateAllCategoriesExist(categories, categoryIds);

        // 순서 변경 요청에 동일 ID가 중복되는지 검증
        validateNoDuplicateCategoryIds(categoryIds);

        // 중복된 sequence 값 검증
        validateNoDuplicateSequences(request);

        for (final var sequenceCommand : request.categories()) {
            final var category = categories.stream()
                    .filter(c -> c.getId().equals(sequenceCommand.id()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            validateCategoryBelongsToWorkspace(workspace, category);
            category.updateSequence(sequenceCommand.sequence());
        }
        workspace.updateLastActivityAt();

        return CategorySequenceUpdateResponse.from(categories);
    }

    @Transactional
    public void deleteRepresentativeCategoryPlace(final MemberAuthInfo memberAuthInfo, final Long categoryId) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        category.updateRepresentativePlaceTo(null);
        category.updateWorkspaceLastActivityAt();
    }

    @Transactional
    public void deleteCategory(final MemberAuthInfo memberAuthInfo, final Long categoryId) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        category.updateWorkspaceLastActivityAt();

        final var categoryPlaces = categoryPlaceRepository.findAllByCategoryIds(List.of(categoryId));
        final var placeIds = categoryPlaces.stream()
                .map(categoryPlace -> categoryPlace.getPlace().getId())
                .toList();

        categoryPlaceRepository.deleteAllByCategoryIds(List.of(categoryId));
        placeRepository.deleteAllByIds(placeIds);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryReadResponse findCategory(final MemberAuthInfo memberAuthInfo, final Long categoryId) {
        final var category = getCategoryById(categoryId);
        category.validateOwnership(memberAuthInfo.id());

        return CategoryReadResponse.from(category);
    }

    @Transactional(readOnly = true)
    public CategoriesReadResponse findAllCategories(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        final var workspace = getWorkspaceByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());
        final var categories = workspace.getCategories();

        return CategoriesReadResponse.from(categories);
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

    private void validateNoDuplicateSequences(final CategoryReorderRequest request) {
        final Set<Integer> sequences = new HashSet<>();
        for (final var sequenceCommand : request.categories()) {
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
