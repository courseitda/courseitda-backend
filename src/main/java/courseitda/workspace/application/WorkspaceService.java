package courseitda.workspace.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.workspace.application.dto.request.CreateWorkspaceCommand;
import courseitda.workspace.application.dto.request.UpdateWorkspaceCommand;
import courseitda.workspace.application.dto.response.CreateWorkspaceResult;
import courseitda.workspace.application.dto.response.ReadWorkspaceResult;
import courseitda.workspace.application.dto.response.ReadWorkspacesByMemberIdResult;
import courseitda.workspace.application.dto.response.UpdateWorkspaceResult;
import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public CreateWorkspaceResult createWorkspace(final Member member, final CreateWorkspaceCommand command) {
        validateDuplicatedTitle(member.getId(), command.title());

        final var workspace = Workspace.createNew(member, command.title());
        final var savedWorkspace = workspaceRepository.save(workspace);

        return CreateWorkspaceResult.from(savedWorkspace);
    }

    @Transactional
    public UpdateWorkspaceResult updateWorkspace(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier,
            final UpdateWorkspaceCommand command
    ) {
        final var workspace = getByIdentifier(workspaceIdentifier);
        final var newTitle = Workspace.formatTitle(command.title());

        workspace.validateOwnership(memberAuthInfo.id());
        // 제목이 변경되는 경우에만 중복 검증
        if (!workspace.getTitle().equals(newTitle)) {
            validateDuplicatedTitle(memberAuthInfo.id(), newTitle);
        }
        workspace.rename(newTitle);

        return UpdateWorkspaceResult.from(workspace);
    }

    @Transactional
    public void deleteWorkspace(final MemberAuthInfo memberAuthInfo, final String workspaceIdentifier) {
        final var workspace = getByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        workspaceRepository.deleteById(workspace.getId());
    }

    @Transactional(readOnly = true)
    public ReadWorkspacesByMemberIdResult readWorkspacesByMemberId(final Long memberId) {
        return ReadWorkspacesByMemberIdResult.from(workspaceRepository.findAllByOwnerId(memberId));
    }

    @Transactional(readOnly = true)
    public ReadWorkspaceResult readWorkspace(final MemberAuthInfo memberAuthInfo, final String workspaceIdentifier) {
        final var workspace = getByIdentifier(workspaceIdentifier);
        workspace.validateOwnership(memberAuthInfo.id());

        return ReadWorkspaceResult.from(workspace);
    }

    private Workspace getByIdentifier(final String identifier) {
        return workspaceRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    private void validateDuplicatedTitle(final Long memberId, final String newTitle) {
        // 해당 회원 소유의 워크스페이스에 이미 해당 타이틀을 사용중인지
        if (workspaceRepository.existsByOwnerIdAndTitle(memberId, newTitle)) {
            throw new BusinessException(ErrorCode.DUPLICATE_WORKSPACE_TITLE);
        }
    }
}
