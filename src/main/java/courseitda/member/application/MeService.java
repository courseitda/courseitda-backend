package courseitda.member.application;

import courseitda.community.domain.SharedCategoryRepository;
import courseitda.member.ui.dto.response.ForkedSharedCategoryIdsResponse;
import courseitda.member.ui.dto.response.MySavedCategoriesResponse;
import courseitda.member.ui.dto.response.MySharedCategoriesResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import java.util.List;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.workspace.domain.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeService {

    private final WorkspaceRepository workspaceRepository;
    private final SavedCategoryRepository savedCategoryRepository;
    private final SharedCategoryRepository sharedCategoryRepository;

    public MyWorkspacesResponse readMyWorkspaces(final Long memberId) {
        return MyWorkspacesResponse.from(workspaceRepository.findAllByOwnerId(memberId));
    }

    @Transactional(readOnly = true)
    public MySavedCategoriesResponse readMySavedCategory(final Long memberId) {
        return MySavedCategoriesResponse.from(savedCategoryRepository.findAllByOwnerId(memberId));
    }

    @Transactional(readOnly = true)
    public MySharedCategoriesResponse readMySharedCategories(final Long memberId) {
        return MySharedCategoriesResponse.from(sharedCategoryRepository.findAllByAuthorId(memberId));
    }

    @Transactional(readOnly = true)
    public ForkedSharedCategoryIdsResponse readForkedSharedCategoryIds(
            final Long memberId,
            final List<Long> sharedCategoryIds
    ) {
        final var forkedIds = savedCategoryRepository.findAllSourceSharedCategoryIdsByOwnerIdAndSourceSharedCategoryIdIn(
                memberId, sharedCategoryIds);
        return ForkedSharedCategoryIdsResponse.from(forkedIds);
    }
}
