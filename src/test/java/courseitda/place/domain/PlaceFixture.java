package courseitda.place.domain;

import java.util.concurrent.atomic.AtomicLong;

public class PlaceFixture {

    public static AtomicLong sequenceName = new AtomicLong(0L);

    public static String anyName() {
        return "place" + sequenceName.incrementAndGet();
    }

    public static String anyRoadAddressName() {
        return "서울특별시 강남구 테헤란로 123";
    }

    public static String anyAddressName() {
        return "서울특별시 강남구 역삼동 123-45";
    }

    public static double anyLatitude() {
        return 37.4979;
    }

    public static double anyLongitude() {
        return 127.0276;
    }

    public static Place anyPlace() {
        return new PlaceBuilder()
                .build();
    }
}
