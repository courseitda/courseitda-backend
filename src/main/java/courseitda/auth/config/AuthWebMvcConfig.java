package courseitda.auth.config;

import courseitda.auth.domain.AuthTokenExtractor;
import courseitda.auth.domain.AuthTokenProvider;
import courseitda.auth.ui.argument.MemberArgumentResolver;
import courseitda.auth.ui.argument.MemberAuthInfoArgumentResolver;
import courseitda.auth.ui.interceptor.AuthRoleCheckInterceptor;
import courseitda.member.application.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthWebMvcConfig implements WebMvcConfigurer {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;
    private final MemberService memberService;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AuthRoleCheckInterceptor(authTokenExtractor, authTokenProvider))
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/image/**", "/login", "/signup", "/");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberAuthInfoArgumentResolver(authTokenExtractor, authTokenProvider));
        resolvers.add(new MemberArgumentResolver(authTokenExtractor, authTokenProvider, memberService));
    }
}
