package courseitda.member.ui;

import courseitda.member.application.MemberService;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.MemberNavigatorViewResponse;
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
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<SignUpResponse> signup(
            @RequestBody @Valid final SignUpRequest request
    ) {
        final var signUpResponse = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + signUpResponse.id()))
                .body(signUpResponse);
    }

    @GetMapping
    public ResponseEntity<MemberNavigatorViewResponse> readMemberNavigatorView(
            final Member member
    ) {
        final var response = new MemberNavigatorViewResponse(member.getNickname());

        return ResponseEntity.ok()
                .body(response);
    }
}
