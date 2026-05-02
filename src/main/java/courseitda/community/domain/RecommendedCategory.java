package courseitda.community.domain;

import courseitda.common.entity.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "recommended_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class RecommendedCategory extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SharedCategory sharedCategory;

    @Builder
    public RecommendedCategory(
            final String imageUrl,
            final SharedCategory sharedCategory
    ) {
        this.imageUrl = imageUrl;
        this.sharedCategory = sharedCategory;
    }

    public static RecommendedCategory createNew(
            final String imageUrl,
            final SharedCategory sharedCategory
    ) {
        return new RecommendedCategory(imageUrl, sharedCategory);
    }
}
