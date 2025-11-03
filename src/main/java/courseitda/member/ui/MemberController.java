package courseitda.member.ui;

import courseitda.member.application.MemberService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        final var response = memberService.signUp(request);

        return ResponseEntity.created(URI.create("/api/members/" + response.id()))
                .body(response);
    }

    @GetMapping("/validations/nickname")
    public ResponseEntity<CheckNicknameDuplicateResponse> checkNicknameDuplicate(
            @RequestParam(required = false) final String value
    ) {
        final var response = memberService.checkNicknameDuplicate(value);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validations/email")
    public ResponseEntity<CheckEmailDuplicateResponse> checkEmailDuplicate(
            @RequestParam(required = false) final String value
    ) {
        final var response = memberService.checkEmailDuplicate(value);

        return ResponseEntity.ok(response);
    }
}
