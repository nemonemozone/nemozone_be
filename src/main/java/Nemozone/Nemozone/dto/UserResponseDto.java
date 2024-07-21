package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserResponseDto {

    private Long userId;
    private Long kakaoId;
    private String kakaoNickname;
    private String nickname;
    private Long relationConnectId;
    private LocalDate createdAt;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.kakaoId = user.getKakaoId();
        this.kakaoNickname = user.getKakaoNickname();
        this.nickname = user.getNickname();
        this.relationConnectId = user.getRelationConnectId();
        this.createdAt = user.getCreatedAt();
    }

    public UserResponseDto(Long userId, Long kakaoId, String kakaoNickname, String nickname, Long relationConnectId, LocalDate createdAt) {
        this.userId = userId;
        this.kakaoId = kakaoId;
        this.kakaoNickname = kakaoNickname;
        this.nickname = nickname;
        this.relationConnectId = relationConnectId;
        this.createdAt = createdAt;
    }
}
