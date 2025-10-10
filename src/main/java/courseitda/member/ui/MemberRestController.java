package courseitda.member.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import courseitda.member.application.MemberService;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<SignUpResponse> create(@RequestBody @Valid final SignUpRequest request) {
        final var signUpResponse = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + signUpResponse.id())).body(signUpResponse);
    }
}
