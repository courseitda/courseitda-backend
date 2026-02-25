package courseitda.community.domain;

import courseitda.place.domain.Place;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "shared_category_places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedCategoryPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SharedCategory sharedCategory;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Place place;

    public SharedCategoryPlace(
            final SharedCategory sharedCategory,
            final Place place
    ) {
        this.sharedCategory = sharedCategory;
        this.place = place;
    }

    public static SharedCategoryPlace createNew(final SharedCategory sharedCategory, final Place place) {
        return new SharedCategoryPlace(sharedCategory, place);
    }
}
