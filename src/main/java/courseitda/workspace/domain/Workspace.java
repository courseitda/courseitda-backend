package courseitda.workspace.domain;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "workspace")
    private List<Category> categories;

    @Builder
    public Workspace(
            final Member member,
            final String title,
            final List<Category> categories
    ) {
        validateTitle(title);

        this.member = member;
        this.title = title;
        this.categories = categories;
    }

    public static Workspace createNew(final Member member, final String title) {
        return new Workspace(member, title, new ArrayList<>());
    }

    public static String formatTitle(final String unformattedTitle) {
        return unformattedTitle.trim();
    }

    public boolean isOwnedBy(final Long memberId) {
        return Objects.equals(this.member.getId(), memberId);
    }

    public void rename(final String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    public void validateOwnership(final Long memberId) {
        if (!isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.WORKSPACE_FORBIDDEN);
        }
    }

    private void validateTitle(final String title) {
        if (title.isBlank()) {
            throw new BusinessException(ErrorCode.WORKSPACE_TITLE_BLANK);
        }
        if (title.length() > 20) {
            throw new BusinessException(ErrorCode.WORKSPACE_TITLE_TOO_LONG);
        }
    }
}
