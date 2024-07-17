package Nemozone.Nemozone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "kakao_nick_name")
    private String kakaoNickname;

    @Setter
    @Column
    private String nickname;

    @Setter
    @Column(name = "relation_connect_id", unique = true)
    private Long relationConnectId;

    @Setter
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "relation_id")
    private Relation relation;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Builder
    public User(Long kakaoId, String kakaoNickname, Relation relation, Long relationConnectId) {
        this.kakaoId = kakaoId;
        this.kakaoNickname = kakaoNickname;
        this.relation = relation;
        this.relationConnectId = relationConnectId;
    }

}