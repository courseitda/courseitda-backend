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

    @OneToMany(mappedBy = "workspace")
    private List<Category> categories;

    @Builder
    public Workspace(
            final Member owner,
            final String identifier,
            final String title,
            final List<Category> categories
    ) {
        validateTitle(title);

        this.owner = owner;
        this.identifier = identifier;
        this.title = title;
        this.categories = categories;
    }

    public static Workspace createNew(final Member owner, final String title) {
        return new Workspace(owner, UUID.randomUUID().toString(), title, new ArrayList<>());
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
}
