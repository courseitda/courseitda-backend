package courseitda.member.ui;

import courseitda.member.application.MemberService;
import courseitda.member.application.dto.request.IsEmailDuplicateCommand;
import courseitda.member.application.dto.request.IsNicknameDuplicateCommand;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.CheckEmailDuplicateResponse;
import courseitda.member.ui.dto.response.CheckNicknameDuplicateResponse;
import courseitda.member.ui.dto.response.SignUpResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<SignUpResponse> signup(
            @RequestBody @Valid final SignUpRequest request
    ) {
        final var result = memberService.create(request.toCommand());

        return ResponseEntity.created(URI.create("/api/members/" + result.id()))
                .body(SignUpResponse.from(result));
    }

    @GetMapping("/check-nickname-duplicate")
    public ResponseEntity<CheckNicknameDuplicateResponse> checkNicknameDuplicate(final String nickname) {
        final var result = memberService.isNicknameDuplicate(new IsNicknameDuplicateCommand(nickname));

        return ResponseEntity.ok(CheckNicknameDuplicateResponse.from(result));
    }

    @GetMapping("/check-email-duplicate")
    public ResponseEntity<CheckEmailDuplicateResponse> checkEmailDuplicate(final String email) {
        final var result = memberService.isEmailDuplicate(new IsEmailDuplicateCommand(email));

        return ResponseEntity.ok(CheckEmailDuplicateResponse.from(result));
    }
}
