package courseitda.workspace.domain;

import courseitda.common.entity.Timestamp;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.CATEGORY_FORBIDDEN);
        }
    }

    private void validateCategoryOwnership(final CategoryPlace candidatePlace) {
        if (!Objects.equals(candidatePlace.getCategory().getId(), this.id)) {
            throw new BusinessException(ErrorCode.CATEGORY_REPRESENTATIVE_PLACE_FORBIDDEN);
        }
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_REQUIRED);
        }
        if (name.length() > 10) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_TOO_LONG);
        }
    }

    private void validateColor(final String color) {
        if (color == null || color.isBlank()) {
            throw new BusinessException(ErrorCode.CATEGORY_COLOR_REQUIRED);
        }
        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new BusinessException(ErrorCode.CATEGORY_COLOR_INVALID);
        }
    }
}
