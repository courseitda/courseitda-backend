package courseitda.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiLoggingAspect {

    private final TraceIdResolver traceIdResolver;
    private final HttpStatusResolver httpStatusResolver;
    private final ApiLogWriter apiLogWriter;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logApiBoundary(final ProceedingJoinPoint pjp) throws Throwable {
        final ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return pjp.proceed();
        }

        final HttpServletRequest request = attrs.getRequest();
        final HttpServletResponse response = attrs.getResponse();

        final long startNanos = System.nanoTime();
        final String traceId = traceIdResolver.resolveAndInject(request);

        // 요청 로그
        apiLogWriter.writeRequestLog(request, traceId);

        try {
            final Object result = pjp.proceed();

            // 응답 로그
            final long endNanos = System.nanoTime();
            final int status = httpStatusResolver.resolveStatus(response, result);
            final long durationMs = (endNanos - startNanos) / 1_000_000L;
            apiLogWriter.writeResponseLog(traceId, status, durationMs);

            // 처리된 에러 로그
            if (status >= 400) {
                apiLogWriter.writeHandledErrorLog(
                        traceId,
                        status,
                        httpStatusResolver.getErrorMessageFromResult(result)
                );
            }
            return result;
        } catch (Throwable t) {
            // 처리되지 않은 에러 로그
            final int status = httpStatusResolver.resolveStatusFromException(t, response);
            apiLogWriter.writeUnhandledErrorLog(
                    traceId,
                    status,
                    httpStatusResolver.getErrorMessage(t)
            );
            throw t;
        } finally {
            traceIdResolver.clear();
        }
    }
}
