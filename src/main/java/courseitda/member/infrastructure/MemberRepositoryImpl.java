package courseitda.member.infrastructure;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public Member save(final Member member) {
        return jpaMemberRepository.save(member);
    }

    @Override
    public Optional<Member> findByEmail(final String email) {
        return jpaMemberRepository.findByEmail(email);
    }

    @Override
    public Optional<Member> findById(final Long id) {
        return jpaMemberRepository.findById(id);
    }
}
