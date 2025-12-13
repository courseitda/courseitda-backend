package courseitda.placesearch.infrastructure.naver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.placesearch.domain.SearchedPlace;

public record NaverPlaceSearchResponse(
        @JsonProperty("lastBuildDate") String lastBuildDate, // 검색 결과를 생성한 시간
        @JsonProperty("total") Integer total,                // 총 검색 결과 개수
        @JsonProperty("start") Integer start,                // 검색 시작 위치
        @JsonProperty("display") Integer display,            // 한 번에 표시할 검색 결과 개수
        @JsonProperty("items") NaverPlaceItem[] items        // 개별 검색 결과
) {

    public record NaverPlaceItem(
            @JsonProperty("title") String name,                  // 업체, 기관의 이름
            @JsonProperty("link") String url,                    // 업체, 기관의 상세 정보 URL
            @JsonProperty("address") String addressName,         // 업체, 기관명의 지번 주소
            @JsonProperty("roadAddress") String roadAddressName, // 업체, 기관명의 도로명 주소
            @JsonProperty("mapx") String longitude,              // 업체, 기관이 위치한 장소의 x 좌표(WGS84 좌표계 기준).
            @JsonProperty("mapy") String latitude                // 업체, 기관이 위치한 장소의 y 좌표(WGS84 좌표계 기준)
    ) {

        private static final double COORDINATE_SCALE = 10000000.0;
        private static final String HTML_TAG_REGEX = "<[^>]*>";

        public SearchedPlace toSearchedPlace() {
            // HTML 태그 제거
            final var cleanName = name.replaceAll(HTML_TAG_REGEX, "");

            // 문자열을 double로 변환 (좌표값 보정)
            // 실제 위도/경도 좌표로 사용하려면 10000000.0으로 나눠야 정상적인 GPS 좌표(127.1234567, 37.4567890)가 됩니다
            final var lat = Double.parseDouble(latitude) / COORDINATE_SCALE;
            final var lng = Double.parseDouble(longitude) / COORDINATE_SCALE;

            return new SearchedPlace(
                    cleanName,
                    url,
                    addressName,
                    roadAddressName,
                    lat,
                    lng
            );
        }
    }
}
