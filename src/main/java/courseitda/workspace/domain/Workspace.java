package courseitda.workspace.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.common.entity.Timestamp;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "workspaces")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Workspace extends Timestamp {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00")
    private LocalDateTime lastActivityAt;

    @OneToMany(mappedBy = "workspace")
    private List<Category> categories;

    @Builder
    public Workspace(
            final Member owner,
            final String identifier,
            final String title,
            final LocalDateTime lastActivityAt,
            final List<Category> categories
    ) {
        validateTitle(title);
        validateLastActivityAt(lastActivityAt);

        this.owner = owner;
        this.identifier = identifier;
        this.title = title;
        this.lastActivityAt = lastActivityAt;
        this.categories = categories;
    }

    public static Workspace createNew(final Member owner, final String title) {
        return new Workspace(owner, UUID.randomUUID().toString(), title, LocalDateTime.now(KST), new ArrayList<>());
    }

    public static String formatTitle(final String unformattedTitle) {
        return unformattedTitle.trim();
    }

    public boolean isOwnedBy(final Long memberId) {
        return Objects.equals(this.owner.getId(), memberId);
    }

    public void rename(final String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    public void updateLastActivityAt() {
        this.lastActivityAt = LocalDateTime.now(KST);
    }

    public void validateOwnership(final Long memberId) {
        if (!isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN);
        }
    }

    private void validateTitle(final String title) {
        if (title.isBlank()) {
            throw new BusinessException(ErrorCode.WORKSPACE_TITLE_EMPTY);
        }
        if (title.length() > 20) {
            throw new BusinessException(ErrorCode.WORKSPACE_TITLE_LENGTH_EXCEEDED);
        }
    }

    private void validateLastActivityAt(final LocalDateTime lastActivityAt) {
        if (lastActivityAt == null) {
            throw new BusinessException(ErrorCode.WORKSPACE_LAST_ACTIVITY_AT_NULL);
        }
    }
}
