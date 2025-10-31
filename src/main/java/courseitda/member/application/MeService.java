package courseitda.member.application;

import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import courseitda.workspace.domain.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeService {

    private final WorkspaceRepository workspaceRepository;

    public MyWorkspacesResponse readMyWorkspaces(final Long memberId) {
        return MyWorkspacesResponse.from(workspaceRepository.findAllByOwnerId(memberId));
    }
}
