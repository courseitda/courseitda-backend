package courseitda.workspace.domain;

import courseitda.common.Timestamp;
import courseitda.exception.BadRequestException;
import courseitda.exception.BusinessRuleException;
import courseitda.exception.ForbiddenException;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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

    @Builder
    public Category(
            final Workspace workspace,
            final List<CategoryPlace> categoryPlaces,
            final String name,
            final String color,
            final Integer sequence,
            final CategoryPlace representativePlace
    ) {
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
        return new Category(workspace, null, name, color, sequence, null);
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

    public void updateNameAndColor(final String newName, final String newColor) {
        validateName(newName);
        validateColor(newColor);
        this.name = newName;
        this.color = newColor;
    }

    public void validateOwnership(final Long memberId) {
        if (!workspace.isOwnedBy(memberId)) {
            throw new ForbiddenException("해당 카테고리를 수정할 권한이 없습니다.");
        }
    }

    private void validateCategoryOwnership(final CategoryPlace candidatePlace) {
        if (!Objects.equals(candidatePlace.getCategory().getId(), this.id)) {
            throw new ForbiddenException("다른 카테고리의 장소를 대표로 지정할 수 없습니다.");
        }
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("카테고리 이름은 필수입니다.");
        }
        if (name.length() > 10) {
            throw new BusinessRuleException("카테고리 이름은 10자를 초과할 수 없습니다.");
        }
    }

    private void validateColor(final String color) {
        if (color == null || color.isBlank()) {
            throw new BadRequestException("카테고리 색상은 필수입니다.");
        }
        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new BusinessRuleException("올바른 색상 형식이 아닙니다.");
        }
    }
}
