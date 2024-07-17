package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
public class RelationInfoResponseDto {

    private Long relationId;
    private MemberInfo member1;
    private MemberInfo member2;
    private Date startDate;
    private Long totalDate;

    public RelationInfoResponseDto(Long relationId, User member1, User member2, Date startDate, Long totalDate) {
        this.relationId = relationId;
        this.member1 = new MemberInfo(member1);
        if (member2 != null)
            this.member2 = new MemberInfo(member2);
        this.startDate = startDate;
        this.totalDate = totalDate;
    }

    @Getter
    public static class MemberInfo {
        private Long id;
        private Long kakaoId;
        private String name;
        private String nickname;

        public MemberInfo(User user) {
            this.id = user.getUserId();
            this.kakaoId = user.getKakaoId();
            this.name = user.getKakaoNickname();
            this.nickname = user.getNickname();
        }

        public MemberInfo(Long id, Long kakaoId, String name, String nickname) {
            this.id = id;
            this.kakaoId = kakaoId;
            this.name = name;
            this.nickname = nickname;
        }
    }
}
