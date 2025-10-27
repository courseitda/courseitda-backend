package courseitda.workspace.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.workspace.application.dto.request.CheckWorkspaceTitleDuplicateCommand;
import courseitda.workspace.application.dto.request.CreateWorkspaceCommand;
import courseitda.workspace.application.dto.request.DeleteWorkspaceCommand;
import courseitda.workspace.application.dto.request.FindWorkspaceCommand;
import courseitda.workspace.application.dto.request.FindWorkspacesByMemberIdCommand;
import courseitda.workspace.application.dto.request.UpdateWorkspaceCommand;
import courseitda.workspace.application.dto.response.CheckTitleDuplicateResult;
import courseitda.workspace.application.dto.response.CreateWorkspaceResult;
import courseitda.workspace.application.dto.response.FindWorkspaceResult;
import courseitda.workspace.application.dto.response.FindWorkspacesByMemberIdResult;
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
    public CreateWorkspaceResult createWorkspace(final CreateWorkspaceCommand command) {
        validateDuplicatedTitle(command.member().getId(), command.title());

        final var workspace = Workspace.createNew(command.member(), command.title());
        final var savedWorkspace = workspaceRepository.save(workspace);

        return CreateWorkspaceResult.from(savedWorkspace);
    }

    @Transactional
    public UpdateWorkspaceResult updateWorkspace(
            final UpdateWorkspaceCommand command
    ) {
        final var workspace = getByIdentifier(command.workspaceIdentifier());
        final var newTitle = Workspace.formatTitle(command.title());

        workspace.validateOwnership(command.memberAuthInfo().id());
        // 제목이 변경되는 경우에만 중복 검증
        if (!workspace.getTitle().equals(newTitle)) {
            validateDuplicatedTitle(command.memberAuthInfo().id(), newTitle);
        }
        workspace.rename(newTitle);

        return UpdateWorkspaceResult.from(workspace);
    }

    @Transactional
    public void deleteWorkspace(final DeleteWorkspaceCommand command) {
        final var workspace = getByIdentifier(command.workspaceIdentifier());
        workspace.validateOwnership(command.memberAuthInfo().id());

        workspaceRepository.deleteById(workspace.getId());
    }

    @Transactional(readOnly = true)
    public FindWorkspacesByMemberIdResult readWorkspacesByMemberId(final FindWorkspacesByMemberIdCommand command) {
        return FindWorkspacesByMemberIdResult.from(workspaceRepository.findAllByOwnerId(command.memberId()));
    }

    @Transactional(readOnly = true)
    public FindWorkspaceResult readWorkspace(final FindWorkspaceCommand command) {
        final var workspace = getByIdentifier(command.workspaceIdentifier());
        workspace.validateOwnership(command.memberAuthInfo().id());

        return FindWorkspaceResult.from(workspace);
    }

    public CheckTitleDuplicateResult checkTitleDuplicate(final CheckWorkspaceTitleDuplicateCommand command) {
        validateTitleNotEmpty(command.title());

        final var isDuplicate = workspaceRepository.existsByOwnerIdAndTitle(
                command.memberAuthInfo().id(),
                command.title()
        );

        return new CheckTitleDuplicateResult(isDuplicate);
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

    private void validateTitleNotEmpty(final String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.WORKSPACE_TITLE_EMPTY);
        }
    }
}
