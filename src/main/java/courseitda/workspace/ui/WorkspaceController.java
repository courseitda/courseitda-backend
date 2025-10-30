package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.workspace.application.CategoryService;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.application.dto.request.CheckWorkspaceTitleDuplicateCommand;
import courseitda.workspace.application.dto.request.DeleteWorkspaceCommand;
import courseitda.workspace.application.dto.request.FindAllCategoriesCommand;
import courseitda.workspace.application.dto.request.FindWorkspaceCommand;
import courseitda.workspace.ui.dto.request.CategoryCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryReorderRequest;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;
import courseitda.workspace.ui.dto.response.CategoriesReadResponse;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategorySequenceUpdateResponse;
import courseitda.workspace.ui.dto.response.CheckTitleDuplicateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceReadResponse;
import courseitda.workspace.ui.dto.response.WorkspaceUpdateResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
@RequiresRole(authRoles = {AuthRole.MEMBER})
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final CategoryService categoryService;

    // 워크스페이스 생성
    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> createWorkspace(
            final Member member,
            @Valid @RequestBody final WorkspaceCreateRequest request
    ) {
        final var response = workspaceService.createWorkspace(
                request.toCommandWith(member)
        );
        return ResponseEntity.created(URI.create("/api/workspaces/" + response.identifier()))
                .body(WorkspaceCreateResponse.from(response));
    }

    // 카테고리 생성
    @PostMapping("/{workspaceIdentifier}/categories")
    public ResponseEntity<CategoryCreateResponse> createCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final CategoryCreateRequest request
    ) {
        final var response = categoryService.createCategory(
                request.toCommandWith(memberAuthInfo, workspaceIdentifier)
        );
        return ResponseEntity.created(
                URI.create("/api/workspaces/"
                        + workspaceIdentifier + "/categories/"
                        + response.id()
                )
        ).body(CategoryCreateResponse.from(response));
    }

    // 워크스페이스 제목 수정
    @PatchMapping("/{workspaceIdentifier}")
    public ResponseEntity<WorkspaceUpdateResponse> updateWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final WorkspaceUpdateRequest request
    ) {
        final var response = workspaceService.updateWorkspace(
                request.toCommandWith(memberAuthInfo, workspaceIdentifier)
        );

        return ResponseEntity.ok(WorkspaceUpdateResponse.from(response));
    }

    // 카테고리 순서 변경
    @PostMapping("/{workspaceIdentifier}/categories/sequence")
    public ResponseEntity<CategorySequenceUpdateResponse> updateCategorySequence(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final CategoryReorderRequest request
    ) {
        final var response = categoryService.updateCategorySequence(
                request.toCommandWith(memberAuthInfo, workspaceIdentifier)
        );
        return ResponseEntity.ok(CategorySequenceUpdateResponse.from(response));
    }

    // 워크스페이스 삭제
    @DeleteMapping("/{workspaceIdentifier}")
    public ResponseEntity<Void> deleteWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        workspaceService.deleteWorkspace(
                new DeleteWorkspaceCommand(memberAuthInfo, workspaceIdentifier)
        );

        return ResponseEntity.noContent().build();
    }

    // 워크스페이스 조회
    @GetMapping("/{workspaceIdentifier}")
    public ResponseEntity<WorkspaceReadResponse> readWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        final var response = workspaceService.readWorkspace(
                new FindWorkspaceCommand(memberAuthInfo, workspaceIdentifier)
        );

        return ResponseEntity.ok(WorkspaceReadResponse.from(response));
    }

    // 카테고리 목록 전체 조회 - 워크스페이스 상세 페이지
    @GetMapping("/{workspaceIdentifier}/categories")
    public ResponseEntity<CategoriesReadResponse> readAllCategories(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        final var response = categoryService.findAllCategories(
                new FindAllCategoriesCommand(memberAuthInfo, workspaceIdentifier)
        );

        return ResponseEntity.ok(CategoriesReadResponse.from(response));
    }

    // 워크스페이스 타이틀 중복 검증
    @GetMapping("/validations/title")
    public ResponseEntity<CheckTitleDuplicateResponse> checkTitleDuplicate(
            final MemberAuthInfo memberAuthInfo,
            @RequestParam(required = false) final String value
    ) {
        final var result = workspaceService.checkTitleDuplicate(
                new CheckWorkspaceTitleDuplicateCommand(memberAuthInfo, value));

        return ResponseEntity.ok(CheckTitleDuplicateResponse.from(result));
    }
}
