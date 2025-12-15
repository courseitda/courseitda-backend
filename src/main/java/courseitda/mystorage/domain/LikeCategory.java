package courseitda.mystorage.domain;

import courseitda.common.entity.Timestamp;
import courseitda.community.domain.SharedSavedCategory;
import courseitda.member.domain.Member;
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
@Table(name = "like_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LikeCategory extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member owner;

    // TODO: (owner id, sharedSavedCategory id)에 대해 unique함 보장 필요
    @ManyToOne
    @JoinColumn(nullable = false)
    private SharedSavedCategory sharedSavedCategory;
}
