package courseitda.community.domain;

import courseitda.common.entity.Timestamp;
import courseitda.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "shared_category_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "shared_category_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedCategoryLike extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SharedCategory sharedCategory;

    public static SharedCategoryLike createNew(final Member member, final SharedCategory sharedCategory) {
        return new SharedCategoryLike(null, member, sharedCategory);
    }
}
