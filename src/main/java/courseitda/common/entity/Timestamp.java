package courseitda.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;

/**
 * 엔티티의 생성/수정 시간을 KST 기준으로 자동 관리하는 기본 클래스. 주의: @PrePersist/@PreUpdate는 JPA 라이프사이클 콜백이므로, Native SQL이나 @Modifying 벌크 연산
 * 사용 시에는 동작하지 않습니다. 이 경우 쿼리에서 직접 타임스탬프를 설정해야 합니다.
 */
@Getter
@MappedSuperclass
public abstract class Timestamp {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @PrePersist
    private void prePersist() {
        final LocalDateTime now = LocalDateTime.now(KST);
        this.createdAt = now;
        this.modifiedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.modifiedAt = LocalDateTime.now(KST);
    }
}
