package courseitda.workspace.application;

import courseitda.member.domain.Member;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public WorkspaceCreateResponse createWorkspace(final Member member, final WorkspaceCreateRequest request) {

        // 400 Bad Request	필수 필드 누락, 유효성 검증
        final var workspace = Workspace.createEmpty(member, request.title());
        final var savedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceCreateResponse.from(savedWorkspace);
    }
}
