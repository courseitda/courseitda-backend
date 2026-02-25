package courseitda.community.domain;

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
@Table(name = "shared_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedCategory extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member author;

    @OneToMany(mappedBy = "sharedCategory")
    private List<SharedCategoryPlace> sharedCategoryPlaces;

    public SharedCategory(
            final String name,
            final Member author,
            final List<SharedCategoryPlace> sharedCategoryPlaces
    ) {
        validateName(name);

        this.name = name;
        this.author = author;
        this.sharedCategoryPlaces = sharedCategoryPlaces;
    }

    public static SharedCategory createNew(final String name, Member author) {
        return new SharedCategory(name, author, new ArrayList<>());
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.SHARED_CATEGORY_NAME_EMPTY);
        }
        if (name.length() > 10) {
            throw new BusinessException(ErrorCode.SHARED_CATEGORY_NAME_LENGTH_EXCEEDED);
        }
    }
}
