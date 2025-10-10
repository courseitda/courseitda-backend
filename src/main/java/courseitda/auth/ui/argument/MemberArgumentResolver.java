package courseitda.auth.ui.argument;

import courseitda.auth.domain.AuthTokenExtractor;
import courseitda.auth.domain.AuthTokenProvider;
import courseitda.exception.auth.AuthenticationException;
import courseitda.member.application.MemberService;
import courseitda.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;
    private final MemberService memberService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String token = authTokenExtractor.extract(request);
        if (!authTokenProvider.isValidToken(token)) {
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }
        final Long memberId = Long.parseLong(authTokenProvider.getPrincipal(token));

        return memberService.findById(memberId);
    }
}
