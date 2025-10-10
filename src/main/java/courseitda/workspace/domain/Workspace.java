package courseitda.workspace.domain;

import courseitda.category.domain.Category;
import courseitda.common.Timestamp;
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

    private void validateTitle(final String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (title.length() > 20) {
            throw new IllegalArgumentException();
        }
    }
}
