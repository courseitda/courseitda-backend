package courseitda.category.domain;

import courseitda.common.Timestamp;
import courseitda.place.domain.Place;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "category_places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryPlace extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Place place;

    private CategoryPlace(
            final Long id,
            final Category category,
            final Place place
    ) {
        this.id = id;
        this.category = category;
        this.place = place;
    }

    public static CategoryPlace createNew(final Category category, final Place place) {
        return new CategoryPlace(null, category, place);
    }
}
