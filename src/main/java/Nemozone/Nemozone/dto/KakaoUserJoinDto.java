package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.User;
import lombok.Getter;

@Getter
public class KakaoUserJoinDto {

    private Long kakaoId;
    private String kakaoNickname;

    public KakaoUserJoinDto(KakaoUserInfoResponseDto userInfo) {
        this.kakaoId = userInfo.id;
        this.kakaoNickname = userInfo.kakaoAccount.profile.nickName;
    }

    public User toEntity() {
        return User.builder()
                .kakaoId(kakaoId)
                .kakaoNickname(kakaoNickname)
                .build();
    }
}
