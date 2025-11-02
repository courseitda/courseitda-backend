package courseitda.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TraceIdResolver {
    private static final String TRACE_ID_KEY = "traceId";          // 요청 및 MDC에서 사용할 키 이름
    private static final String TRACE_ID_HEADER = "X-Trace-Id";    // 클라이언트가 보낼 수 있는 traceId 헤더 이름

    // 요청, 헤더, MDC 순으로 traceId를 확인하고 없으면 생성하여 주입
    public String resolveAndInject(final HttpServletRequest request) {
        // 요청 attribute에 traceId가 이미 있으면 그대로 사용
        final Object attribute = request.getAttribute(TRACE_ID_KEY);
        if (attribute instanceof String s && StringUtils.hasText(s)) {
            MDC.put(TRACE_ID_KEY, s); // MDC 에도 동일 값 주입
            return s;
        }

        // MDC에 traceId가 있으면 request에도 반영
        final String mdc = MDC.get(TRACE_ID_KEY);
        if (StringUtils.hasText(mdc)) {
            request.setAttribute(TRACE_ID_KEY, mdc);
            return mdc;
        }

        // HTTP 헤더(X-Trace-Id)에서 traceId를 가져오거나 없으면 새로 생성
        final String header = Optional.ofNullable(request.getHeader(TRACE_ID_HEADER))
                .filter(StringUtils::hasText)
                .orElseGet(this::shortRandomId);

        // 결정된 traceId를 request와 MDC 모두에 저장
        request.setAttribute(TRACE_ID_KEY, header);
        MDC.put(TRACE_ID_KEY, header);
        return header;
    }

    // 요청이 끝나면 MDC 에서 traceId 제거 (스레드 재사용 시 오염 방지)
    public void clear() {
        MDC.remove(TRACE_ID_KEY);
    }

    // 8자리 랜덤 traceId 생성 (UUID 기반, 하이픈 제거 후 앞 8자리 사용)
    private String shortRandomId() {
        final String raw = UUID.randomUUID().toString().replace("-", "");
        return raw.substring(0, Math.min(8, raw.length()));
    }
}
