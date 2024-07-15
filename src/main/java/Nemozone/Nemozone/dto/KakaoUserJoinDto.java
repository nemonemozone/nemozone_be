package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.User;
import lombok.Getter;

@Getter
public class KakaoUserJoinDto {

    private Long kakaoId;
    private String nickname;

    public KakaoUserJoinDto(KakaoUserInfoResponseDto userInfo) {
        this.kakaoId = userInfo.id;
        this.nickname = userInfo.kakaoAccount.profile.nickName;
    }

    public User toEntity() {
        return User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .build();
    }
}
