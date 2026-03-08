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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "saved_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SQLRestriction("deleted_at IS NULL")
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

    @Column
    private Long sourceSharedCategoryId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public SavedCategory(
            final Member owner,
            final String name,
            final List<SavedCategoryPlace> savedCategoryPlaces,
            final Long sourceSharedCategoryId
    ) {
        validateName(name);

        this.owner = owner;
        this.name = name;
        this.savedCategoryPlaces = savedCategoryPlaces;
        this.sourceSharedCategoryId = sourceSharedCategoryId;
    }

    public static SavedCategory createNew(final Member owner, final String name) {
        return new SavedCategory(owner, name, new ArrayList<>(), null);
    }

    public static SavedCategory createFromShared(final Member owner, final String name,
                                                 final Long sourceSharedCategoryId) {
        return new SavedCategory(owner, name, new ArrayList<>(), sourceSharedCategoryId);
    }

    public boolean hasSource() {
        return this.sourceSharedCategoryId != null;
    }

    public void updateName(final String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void validateOwnership(final Long memberId) {
        if (!Objects.equals(this.owner.getId(), memberId)) {
            throw new BusinessException(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN);
        }
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
