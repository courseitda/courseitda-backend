package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
@RequiresRole(authRoles = {AuthRole.MEMBER})
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    // 워크스페이스 생성
    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> createWorkspace(
            final Member member,
            @Valid @RequestBody final WorkspaceCreateRequest request
    ) {
        // ✅ 201 Created	워크스페이스 생성 성공
        // ✅ 400 Bad Request	필수 필드 누락, 유효성 검증 실패 -> request dto에서 검증 진행
        // ✅ 401 Unauthorized	로그인하지 않은 사용자의 생성 요청 -> Member 받아오는 과정에서 알아서 처리

        final var response = workspaceService.createWorkspace(
                member,
                request.toCommand()
        );
        return ResponseEntity.created(URI.create("/api/workspaces/" + response.identifier()))
                .body(WorkspaceCreateResponse.from(response));
    }

    // 워크스페이스 제목 수정
    @PatchMapping("/{workspaceIdentifier}")
    public ResponseEntity<WorkspaceUpdateResponse> updateWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final WorkspaceUpdateRequest request
    ) {
        // ✅ 200 OK	        이름 변경 성공
        // ✅ 400 Bad Request	필수 필드 누락, 유효성 검증 실패 -> request dto에서 검증 진행
        // ✅ 401 Unauthorized	토큰 누락 또는 유효하지 않은 토큰 -> MemberAuthInfo 받아오는 과정에서 알아서 처리
        // ✅ 403 Forbidden	    유효한 토큰을 갖고 있지만, 해당 워크스페이스의 수정 권한이 없음 -> 워크스페이스 소유자 여부 예외 처리
        // ✅ 404 Not Found	    워크스페이스 ID가 존재하지 않음 -> findById 예외 처리
        // ✅ 409 Conflict      워크스페이스 삭제 과정에서 충돌이 일어난 경우 -> 라젤 추가) 동일한 워크스페이스 닉네임이 존재하는 경우

        final var response = workspaceService.updateWorkspace(
                memberAuthInfo,
                workspaceIdentifier,
                request.toCommand()
        );

        return ResponseEntity.ok(WorkspaceUpdateResponse.from(response));
    }

    // 워크스페이스 삭제
    @DeleteMapping("/{workspaceIdentifier}")
    public ResponseEntity<Void> deleteWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        // ✅ 204 No Content	워크스페이스 삭제 성공
        // ✅ 401 Unauthorized	토큰 누락 또는 유효하지 않은 토큰 -> MemberAuthInfo 받아오는 과정에서 알아서 처리
        // ✅ 403 Forbidden	    유효한 토큰을 갖고 있지만, 해당 워크스페이스의 삭제 권한이 없음 -> 워크스페이스 소유자 여부 예외 처리
        // ✅ 404 Not Found	    워크스페이스 ID가 존재하지 않음 -> findById 예외 처리
        // ❌ 409 Conflict	    무언가 제약 조건 때문에 워크스페이스 삭제가 불가한 상태 -> 공유 기능있으면 들어가야 한다.

        workspaceService.deleteWorkspace(memberAuthInfo, workspaceIdentifier);

        return ResponseEntity.noContent().build();
    }

    // 워크스페이스 조회
    @GetMapping("/{workspaceIdentifier}")
    public ResponseEntity<WorkspaceReadResponse> readWorkspace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        // ✅ 200 OK	        워크스페이스 조회 성공
        // ✅ 401 Unauthorized	토큰 누락 또는 유효하지 않은 토큰 -> MemberAuthInfo 받아오는 과정에서 알아서 처리
        // ✅ 403 Forbidden	    유효한 토큰을 갖고 있지만, 해당 워크스페이스의 조회 권한이 없음 -> 워크스페이스 소유자 여부 예외 처리
        // ✅ 404 Not Found	    워크스페이스 ID가 존재하지 않음 -> findById 예외 처리

        final var response = workspaceService.readWorkspace(memberAuthInfo, workspaceIdentifier);

        return ResponseEntity.ok(WorkspaceReadResponse.from(response));
    }
}
