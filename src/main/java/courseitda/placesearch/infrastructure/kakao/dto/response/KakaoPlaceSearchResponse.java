package courseitda.placesearch.infrastructure.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.placesearch.domain.SearchedPlace;
import java.util.List;

public record KakaoPlaceSearchResponse(
        @JsonProperty("documents") List<Document> documents,
        @JsonProperty("meta") Meta meta
) {

    public record Document(
            @JsonProperty("id") String id,
            @JsonProperty("place_name") String name,
            @JsonProperty("address_name") String addressName,
            @JsonProperty("road_address_name") String roadAddressName,
            @JsonProperty("y") Double latitude,
            @JsonProperty("x") Double longitude
    ) {

        public SearchedPlace toSearchedPlace() {
            return new SearchedPlace(
                    this.name,
                    this.addressName,
                    this.roadAddressName,
                    this.latitude,
                    this.longitude
            );
        }
    }

    public record Meta(
            @JsonProperty("total_count") int totalCount,
            @JsonProperty("pageable_count") int pageableCount,
            @JsonProperty("is_end") boolean isEnd
    ) {
    }
}
