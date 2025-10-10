package courseitda.member.domain;

import java.util.concurrent.atomic.AtomicLong;

public class MemberFixture {

    public static AtomicLong sequenceNickName = new AtomicLong(0L);
    public static AtomicLong sequenceEmail = new AtomicLong(0L);
    public static AtomicLong sequenceLoginAuthenticationIdentifier = new AtomicLong(0L);

    public static String anyNickname() {
        return "nickname" + sequenceNickName.incrementAndGet();
    }

    public static String anyEmail() {
        return "email" + sequenceEmail.incrementAndGet() + "@test.com";
    }

    public static String anyPassword() {
        return "password";
    }

    public static String anyProfileImageUrl() {
        return "https://courseitda.me/default.png"; // 존재하지 않는 리소스
    }

    public static Member anyMember() {
        return new MemberBuilder()
                .build();
    }

    public static String anyLoginAuthenticationIdentifier() {
        return "authId" + sequenceLoginAuthenticationIdentifier.incrementAndGet();
    }
}
