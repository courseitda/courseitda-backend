package courseitda.category.application;

import courseitda.category.domain.Category;
import courseitda.category.domain.CategoryRepository;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryReorderRequest;
import courseitda.category.ui.dto.request.CategorySequenceRequest;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryReorderResponse;
import courseitda.exception.ForbiddenException;
import courseitda.exception.NotFoundException;
import courseitda.member.domain.Member;
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
            final Member member,
            final Long workspaceId,
            final CategoryCreateRequest request
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        validateOwnership(member, workspace);

        // N+1 문제 해결: workspace.getCategories().size() 대신 직접 count 쿼리 사용
        final var nextSequence = categoryRepository.countByWorkspaceId(workspaceId) + 1;
        final var category = Category.createNew(workspace, request.name(), request.color(), nextSequence);
        final var savedCategory = categoryRepository.save(category);

        return CategoryCreateResponse.from(savedCategory);
    }

    @Transactional
    public CategoryReorderResponse updateCategorySequence(
            final Member member,
            final Long workspaceId,
            final CategoryReorderRequest request
    ) {
        final var workspace = getWorkspaceById(workspaceId);
        validateOwnership(member, workspace);

        final var categoryIds = request.categorySequenceRequests().stream()
                .map(CategorySequenceRequest::id)
                .toList();

        final var categories = categoryRepository.findAllById(categoryIds);

        // 요청된 카테고리 ID 개수와 실제 조회된 카테고리 개수가 일치하는지 검증
        validateAllCategoriesExist(categories, categoryIds);

        // 중복된 sequence 값 검증
        validateNoDuplicateSequences(request);

        for (final var sequenceRequest : request.categorySequenceRequests()) {
            final var category = categories.stream()
                    .filter(c -> c.getId().equals(sequenceRequest.id()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("ID에 해당하는 카테고리를 찾을 수 없습니다."));

            validateCategoryOwnership(workspace, category);
            category.updateSequence(sequenceRequest.sequence());
        }

        return CategoryReorderResponse.from(categories);
    }

    private Workspace getWorkspaceById(final Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 워크스페이스를 찾을 수 없습니다."));
    }

    private void validateOwnership(final Member member, final Workspace workspace) {
        if (!workspace.isOwnedBy(member)) {
            throw new ForbiddenException("해당 워크스페이스의 수정 권한이 없습니다.");
        }
    }

    private void validateAllCategoriesExist(List<Category> categories, List<Long> categoryIds) {
        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException("일부 카테고리를 찾을 수 없습니다.");
        }
    }

    private void validateNoDuplicateSequences(CategoryReorderRequest request) {
        final Set<Integer> sequences = new HashSet<>();
        for (final var sequenceRequest : request.categorySequenceRequests()) {
            if (!sequences.add(sequenceRequest.sequence())) {
                throw new IllegalArgumentException("중복된 순서 값이 있습니다.");
            }
        }
    }

    private void validateCategoryOwnership(final Workspace workspace, final Category category) {
        if (!category.getWorkspace().getId().equals(workspace.getId())) {
            throw new ForbiddenException("해당 워크스페이스에 속한 카테고리가 아닙니다.");
        }
    }
}
