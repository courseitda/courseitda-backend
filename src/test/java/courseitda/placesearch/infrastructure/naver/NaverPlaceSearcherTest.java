package courseitda.placesearch.infrastructure.naver;

import static org.assertj.core.api.Assertions.assertThat;

import courseitda.placesearch.domain.SearchedPlace;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("NaverPlaceSearcher 테스트")
@Disabled("실제 API 호출 테스트가 필요할 때만 활성화")
class NaverPlaceSearcherTest {

    @Autowired
    private NaverPlaceSearcher naverPlaceSearcher;

    @Test
    @DisplayName("네이버 API를 통해 장소를 검색하면 SearchedPlace 목록을 반환한다")
    void searchPlaces() {
        // given
        final String keyword = "잠실역";
        final Integer size = 5;

        // when
        final List<SearchedPlace> searchedPlaces = naverPlaceSearcher.searchPlaces(keyword, size);

        // then
        for (final SearchedPlace searchedPlace : searchedPlaces) {
            System.out.println(searchedPlace);
        }
        assertThat(searchedPlaces).isNotEmpty();
        assertThat(searchedPlaces.size()).isLessThanOrEqualTo(5);
    }
}
