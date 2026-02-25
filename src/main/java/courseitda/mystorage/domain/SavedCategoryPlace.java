package courseitda.mystorage.domain;

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
@Table(name = "saved_category_places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SavedCategoryPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SavedCategory savedCategory;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Place place;

    public SavedCategoryPlace(
            final SavedCategory savedCategory,
            final Place place
    ) {
        this.savedCategory = savedCategory;
        this.place = place;
    }

    public static SavedCategoryPlace createNew(final SavedCategory savedCategory, final Place place) {
        return new SavedCategoryPlace(savedCategory, place);
    }
}
