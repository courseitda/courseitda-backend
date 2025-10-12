package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.ConflictException;
import courseitda.common.exception.NotFoundException;
import courseitda.member.domain.Member;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public WorkspaceCreateResponse createWorkspace(final Member member, final WorkspaceCreateRequest request) {
        validateDuplicatedTitle(member.getId(), request.title());

        final var workspace = Workspace.createNew(member, request.title());
        final var savedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceCreateResponse.from(savedWorkspace);
    }

    @Transactional
    public WorkspaceUpdateResponse updateWorkspace(
            final MemberAuthInfo memberAuthInfo,
            final Long workspaceId,
            final WorkspaceUpdateRequest request
    ) {
        final var workspace = getById(workspaceId);
        final var newTitle = Workspace.formatTitle(request.title());

        workspace.validateOwnership(memberAuthInfo.id());
        // 제목이 변경되는 경우에만 중복 검증
        if (!workspace.getTitle().equals(newTitle)) {
            validateDuplicatedTitle(memberAuthInfo.id(), newTitle);
        }
        workspace.rename(newTitle);

        return WorkspaceUpdateResponse.from(workspace);
    }

    @Transactional
    public void deleteWorkspace(final MemberAuthInfo memberAuthInfo, final Long workspaceId) {
        final var workspace = getById(workspaceId);
        workspace.validateOwnership(memberAuthInfo.id());

        workspaceRepository.deleteById(workspaceId);
    }

    private Workspace getById(final Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 워크스페이스를 찾을 수 없습니다."));
    }

    private void validateDuplicatedTitle(final Long memberId, final String newTitle) {
        // 해당 회원 소유의 워크스페이스에 이미 해당 타이틀을 사용중인지
        if (workspaceRepository.existsByMemberIdAndTitle(memberId, newTitle)) {
            throw new ConflictException(newTitle + "은(는) 이미 사용중 입니다.");
        }
    }
}
