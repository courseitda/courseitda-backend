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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Column(name = "root_shared_category_id")
    private Long rootSharedCategoryId;

    @Column(name = "parent_shared_category_id")
    private Long parentSharedCategoryId;

    @Builder
    public SharedCategory(
            final String name,
            final Member author,
            final List<SharedCategoryPlace> sharedCategoryPlaces,
            final Long rootSharedCategoryId,
            final Long parentSharedCategoryId
    ) {
        validateName(name);

        this.name = name;
        this.author = author;
        this.sharedCategoryPlaces = sharedCategoryPlaces;
        this.rootSharedCategoryId = rootSharedCategoryId;
        this.parentSharedCategoryId = parentSharedCategoryId;
    }

    public static SharedCategory createRoot(final String name, final Member author) {
        return new SharedCategory(name, author, new ArrayList<>(), null, null);
    }

    public static SharedCategory createChild(
            final String name,
            final Member author,
            final Long rootSharedCategoryId,
            final Long parentSharedCategoryId
    ) {
        return new SharedCategory(name, author, new ArrayList<>(), rootSharedCategoryId, parentSharedCategoryId);
    }

    public void initializeRoot() {
        if (this.rootSharedCategoryId == null) {
            this.rootSharedCategoryId = this.id;
        }
    }

    public void validateOwnership(final Long memberId) {
        if (!Objects.equals(this.author.getId(), memberId)) {
            throw new BusinessException(ErrorCode.SHARED_CATEGORY_MODIFY_FORBIDDEN);
        }
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
