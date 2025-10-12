package courseitda.placesearch.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.RequiresRole;
import courseitda.placesearch.application.PlaceSearchService;
import courseitda.placesearch.ui.dto.response.SearchedPlacesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaceSearchController {

    private final PlaceSearchService placeSearchService;

    @RequiresRole(authRoles = MEMBER)
    @GetMapping("/api/places/search")
    public ResponseEntity<SearchedPlacesResponse> searchPlaces(
            @RequestParam("keyword") final String keyword
    ) {
        final SearchedPlacesResponse searchedPlacesResponse = placeSearchService.search(keyword);

        return ResponseEntity.ok(searchedPlacesResponse);
    }
}
