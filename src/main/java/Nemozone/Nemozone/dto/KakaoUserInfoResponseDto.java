package Nemozone.Nemozone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Getter
@NoArgsConstructor //역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponseDto {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("has_signed_up")
    public Boolean hasSignedUp;

    @JsonProperty("connected_at")
    public Date connectedAt;

    @JsonProperty("synched_at")
    public Date synchedAt;

    @JsonProperty("properties")
    public HashMap<String, String> properties;

    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount {

        @JsonProperty("profile_needs_agreement")
        public Boolean isProfileAgree;

        @JsonProperty("profile_nickname_needs_agreement")
        public Boolean isNickNameAgree;

        @JsonProperty("profile")
        public Profile profile;


        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Profile {

            //닉네임
            @JsonProperty("nickname")
            public String nickName;

        }
    }

}
