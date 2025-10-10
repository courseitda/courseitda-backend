package courseitda.category.domain;

import courseitda.common.Timestamp;
import courseitda.exception.ForbiddenException;
import courseitda.member.domain.Member;
import courseitda.workspace.domain.Workspace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Workspace workspace;

    @OneToMany(mappedBy = "category")
    private List<CategoryPlace> categoryPlaces;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Integer sequence;

    @OneToOne
    @JoinColumn(
            name = "representative_place_id", foreignKey = @ForeignKey(name = "fk_category_representative_place")
    )
    private CategoryPlace representativePlace;

    private Category(
            final Long id,
            final Workspace workspace,
            final List<CategoryPlace> categoryPlaces,
            final String name,
            final String color,
            final Integer sequence,
            final CategoryPlace representativePlace
    ) {
        this.id = id;
        this.workspace = workspace;
        this.categoryPlaces = categoryPlaces;
        this.name = name;
        this.color = color;
        this.sequence = sequence;
        this.representativePlace = representativePlace;
    }

    public static Category createNew(
            final Workspace workspace,
            final String name,
            final String color,
            final Integer sequence
    ) {
        return new Category(null, workspace, null, name, color, sequence, null);
    }

    public void updateRepresentativePlaceTo(final CategoryPlace candidatePlace) {
        if (candidatePlace == null) {
            this.representativePlace = null;
            return;
        }
        validateCategoryOwnership(candidatePlace);
        this.representativePlace = candidatePlace;
    }

    public void updateSequence(final Integer newSequence) {
        this.sequence = newSequence;
    }

    public boolean isOwnedBy(final Member member) {
        return workspace.isOwnedBy(member);
    }

    private void validateCategoryOwnership(CategoryPlace candidatePlace) {
        if (!Objects.equals(candidatePlace.getCategory().getId(), this.id)) {
            throw new ForbiddenException("다른 카테고리의 장소를 대표로 지정할 수 없습니다.");
        }
    }
}
