package courseitda.member.application;

import courseitda.member.ui.dto.response.MySavedCategoriesResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
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

    public MyWorkspacesResponse readMyWorkspaces(final Long memberId) {
        return MyWorkspacesResponse.from(workspaceRepository.findAllByOwnerId(memberId));
    }

    @Transactional(readOnly = true)
    public MySavedCategoriesResponse readMySavedCategory(final Long memberId) {
        return MySavedCategoriesResponse.from(savedCategoryRepository.findAllByOwnerId(memberId));
    }
}
