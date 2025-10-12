package courseitda.workspace.domain;

import courseitda.category.domain.Category;
import courseitda.common.Timestamp;
import courseitda.exception.BadRequestException;
import courseitda.exception.BusinessRuleException;
import courseitda.exception.ForbiddenException;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "workspaces")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Workspace(
            final Long id,
            final Member member,
            final String title,
            final List<Category> categories
    ) {
        validateTitle(title);

        this.id = id;
        this.member = member;
        this.title = title;
        this.categories = categories;
    }

    public static Workspace createEmpty(final Member member, final String title) {
        return new Workspace(null, member, title, new ArrayList<>());
    }

    public static String formatTitle(final String unformattedTitle) {
        return unformattedTitle.trim();
    }

    public boolean isOwnedBy(final Member member) {
        return Objects.equals(this.member.getId(), member.getId());
    }

    public void rename(final String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    public void validateOwnership(final Member member) {
        if (!isOwnedBy(member)) {
            throw new ForbiddenException("해당 워크스페이스의 수정 권한이 없습니다.");
        }
    }

    private void validateTitle(final String title) {
        if (title.isBlank()) {
            throw new BadRequestException("워크스페이스 제목은 공백일 수 없습니다.");
        }
        if (title.length() > 20) {
            throw new BusinessRuleException("워크스페이스 제목은 20자 이하이어야 합니다.");
        }
    }
}
