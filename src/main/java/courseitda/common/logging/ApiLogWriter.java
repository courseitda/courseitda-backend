package courseitda.common.logging;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApiLogWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ApiLogWriter.class);

    private static final Marker REQUEST = MarkerFactory.getMarker("REQUEST");
    private static final Marker RESPONSE = MarkerFactory.getMarker("RESPONSE");
    private static final Marker ERROR = MarkerFactory.getMarker("ERROR");

    public void writeRequestLog(
            final HttpServletRequest req,
            final String traceId
    ) {
        LOG.info(
                REQUEST,
                "API 요청 로그",
                keyValue("traceId", traceId),
                keyValue("clientIp", getClientIp(req)),
                keyValue("httpMethod", req.getMethod()),
                keyValue("url", req.getRequestURI())
        );
    }

    public void writeResponseLog(
            final String traceId,
            final int httpStatus,
            final long durationMs
    ) {
        LOG.info(
                RESPONSE,
                "API 응답 로그",
                keyValue("traceId", traceId),
                keyValue("httpStatus", httpStatus),
                keyValue("duration_ms", durationMs)
        );
    }

    public void writeHandledErrorLog(
            final String traceId,
            final int status,
            final String reason
    ) {
        LOG.error(
                ERROR,
                "API 에러 로그",
                keyValue("traceId", traceId),
                keyValue("httpStatus", status),
                keyValue("message", reason)
        );
    }

    public void writeUnhandledErrorLog(
            final String traceId,
            final int status,
            final String message
    ) {
        LOG.error(
                ERROR,
                "API 미처리 에러 로그",
                keyValue("traceId", traceId),
                keyValue("httpStatus", status),
                keyValue("message", message)
        );
    }

    private String getClientIp(final HttpServletRequest request) {
        final String fwd = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(fwd)) {
            return fwd.split(",")[0].trim();
        }

        final String real = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(real)) {
            return real;
        }

        return request.getRemoteAddr();
    }
}
