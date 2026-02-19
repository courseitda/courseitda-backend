package courseitda.mystorage.domain;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "saved_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SavedCategory extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "savedCategory")
    private List<SavedCategoryPlace> savedCategoryPlaces;

    public SavedCategory(
            final Member owner,
            final String name,
            final List<SavedCategoryPlace> savedCategoryPlaces
    ) {
        validateName(name);

        this.owner = owner;
        this.name = name;
        this.savedCategoryPlaces = savedCategoryPlaces;
    }

    public static SavedCategory createNew(final Member owner, final String name) {
        return new SavedCategory(owner, name, new ArrayList<>());
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.SAVED_CATEGORY_NAME_EMPTY);
        }
        if (name.length() > 10) {
            throw new BusinessException(ErrorCode.SAVED_CATEGORY_NAME_LENGTH_EXCEEDED);
        }
    }
}
