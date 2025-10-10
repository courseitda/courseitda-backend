package courseitda.workspace.ui;

import courseitda.member.domain.Member;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    // 워크스페이스 생성
    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> createWorkspace(
            // TODO: 헤더 인증 필요
            Member member,
            @RequestBody WorkspaceCreateRequest request
    ) {
        // ✅ 201 Created	워크스페이스 생성 성공
        //    400 Bad Request	필수 필드 누락, 유효성 검증 실패
        // ✅ 401 Unauthorized	로그인하지 않은 사용자의 생성 요청 -> Member 받아오는 과정에서 알아서 처리

        WorkspaceCreateResponse response = workspaceService.createWorkspace(member, request);
        return ResponseEntity.created(URI.create("/api/workspaces/" + response.id()))
                .body(response);
    }
}
