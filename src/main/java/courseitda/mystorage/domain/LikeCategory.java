package courseitda.mystorage.domain;

import courseitda.common.entity.Timestamp;
import courseitda.community.domain.SharedCategory;
import courseitda.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "likeCategory")
    private List<LikeCategoryPlace> likeCategoryPlaces;

    // TODO: (owner id, sharedCategory id)에 대해 unique함 보장 필요

    @ManyToOne
    @JoinColumn()
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private SharedCategory sharedCategory;
}
