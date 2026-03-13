package courseitda.member.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryRepository;
import courseitda.member.ui.dto.response.ForkedSharedCategoryIdsResponse;
import courseitda.member.ui.dto.response.MySavedCategoriesResponse;
import courseitda.member.ui.dto.response.MySharedCategoriesResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeService {

    private final WorkspaceRepository workspaceRepository;
    private final SavedCategoryRepository savedCategoryRepository;
    private final SharedCategoryRepository sharedCategoryRepository;

    @Transactional(readOnly = true)
    public MyWorkspacesResponse readMyWorkspaces(final Long memberId, final Long cursor, final int size) {
        validatePageSize(size);
        final var workspaces = findWorkspacesByCursor(memberId, cursor, size + 1);
        final boolean hasNext = workspaces.size() > size;

        if (hasNext) {
            return MyWorkspacesResponse.from(workspaces.subList(0, size), hasNext, workspaces.get(size - 1).getId());
        }
        return MyWorkspacesResponse.from(workspaces, hasNext, null);
    }

    @Transactional(readOnly = true)
    public MySavedCategoriesResponse readMySavedCategories(final Long memberId, final Long cursor, final int size) {
        validatePageSize(size);
        final var savedCategories = findSavedCategoriesByCursor(memberId, cursor, size + 1);
        final boolean hasNext = savedCategories.size() > size;
        final var publishableIds = findPublishableSavedCategoryIds(savedCategories);

        if (hasNext) {
            return MySavedCategoriesResponse.from(savedCategories.subList(0, size), publishableIds, hasNext,
                    savedCategories.get(size - 1).getId());
        }
        return MySavedCategoriesResponse.from(savedCategories, publishableIds, hasNext, null);
    }

    @Transactional(readOnly = true)
    public MySharedCategoriesResponse readMySharedCategories(final Long memberId, final Long cursor, final int size) {
        validatePageSize(size);
        final var sharedCategories = findMySharedCategoriesByCursor(memberId, cursor, size + 1);
        final boolean hasNext = sharedCategories.size() > size;

        if (hasNext) {
            return MySharedCategoriesResponse.from(sharedCategories.subList(0, size), hasNext,
                    sharedCategories.get(size - 1).getId());
        }
        return MySharedCategoriesResponse.from(sharedCategories, hasNext, null);
    }

    @Transactional(readOnly = true)
    public ForkedSharedCategoryIdsResponse readForkedSharedCategoryIds(
            final Long memberId,
            final List<Long> sharedCategoryIds
    ) {
        final var forkedIds = savedCategoryRepository
                .findAllSourceSharedCategoryIdsByOwnerIdAndSourceSharedCategoryIdIn(
                        memberId, sharedCategoryIds);
        return ForkedSharedCategoryIdsResponse.from(forkedIds);
    }

    private Set<Long> findPublishableSavedCategoryIds(final List<SavedCategory> savedCategories) {
        final var sourceSharedCategoryIds = savedCategories.stream()
                .filter(SavedCategory::hasSource)
                .map(SavedCategory::getSourceSharedCategoryId)
                .toList();

        if (sourceSharedCategoryIds.isEmpty()) {
            return savedCategories.stream()
                    .map(SavedCategory::getId)
                    .collect(Collectors.toSet());
        }

        final var sourceMap = sharedCategoryRepository.findAllByIdInIncludingDeleted(sourceSharedCategoryIds).stream()
                .collect(Collectors.toMap(SharedCategory::getId, sc -> sc));

        return savedCategories.stream()
                .filter(sc -> hasChangesFromSource(sc, sourceMap))
                .map(SavedCategory::getId)
                .collect(Collectors.toSet());
    }

    private boolean hasChangesFromSource(final SavedCategory savedCategory, final Map<Long, SharedCategory> sourceMap) {
        if (!savedCategory.hasSource()) {
            return true;
        }
        final var sourceSharedCategory = sourceMap.get(savedCategory.getSourceSharedCategoryId());
        if (sourceSharedCategory == null) {
            return true;
        }

        final Set<Long> sourcePlaceIds = sourceSharedCategory.getSharedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());
        final Set<Long> currentPlaceIds = savedCategory.getSavedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());

        return !sourcePlaceIds.equals(currentPlaceIds);
    }

    private void validatePageSize(final int size) {
        if (size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_SHARED_CATEGORY_SIZE);
        }
    }

    private List<Workspace> findWorkspacesByCursor(final Long ownerId, final Long cursor, final int limit) {
        if (cursor == null) {
            return workspaceRepository.findAllByOwnerIdOrderByIdDesc(ownerId, limit);
        }
        return workspaceRepository.findAllByOwnerIdAndIdLessThanOrderByIdDesc(ownerId, cursor, limit);
    }

    private List<SavedCategory> findSavedCategoriesByCursor(final Long ownerId, final Long cursor, final int limit) {
        if (cursor == null) {
            return savedCategoryRepository.findAllByOwnerIdOrderByIdDesc(ownerId, limit);
        }
        return savedCategoryRepository.findAllByOwnerIdAndIdLessThanOrderByIdDesc(ownerId, cursor, limit);
    }

    private List<SharedCategory> findMySharedCategoriesByCursor(final Long authorId, final Long cursor,
            final int limit) {
        if (cursor == null) {
            return sharedCategoryRepository.findAllByAuthorIdOrderByIdDesc(authorId, limit);
        }
        return sharedCategoryRepository.findAllByAuthorIdAndIdLessThanOrderByIdDesc(authorId, cursor, limit);
    }
}
