package courseitda.workspace.application.dto.response;

import courseitda.place.domain.Place;
import courseitda.workspace.domain.CategoryPlace;

public record CreateCategoryPlaceResult(
        Long id,
        Place place
) {

    public static CreateCategoryPlaceResult from(final CategoryPlace categoryPlace) {
        return new CreateCategoryPlaceResult(
                categoryPlace.getId(),
                categoryPlace.getPlace()
        );
    }
}
