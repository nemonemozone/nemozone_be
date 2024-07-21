package Nemozone.Nemozone.service;

import Nemozone.Nemozone.dto.KakaoTokenResponseDto;
import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.KakaoUserJoinDto;
import Nemozone.Nemozone.dto.UserJoinDto;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.repository.UserRepository;
import Nemozone.Nemozone.session.SessionConst;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class UserService {
    private final String clientId;
    private final UserRepository userRepository;
    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    private static final Integer RANDOM_BOUND = 1000000;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Autowired
    public UserService(@Value("${kakao.client-id}") String clientId, UserRepository userRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
    }

    public String getAccessTokenFromKakao(String code) {

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();


        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());

        return userInfo;
    }

    public User kakaoLogin(KakaoUserInfoResponseDto userInfo, HttpSession session, String kakaoAccessToken) {
        Long kakaoUserId = userInfo.id;
        String nickname = userInfo.kakaoAccount.profile.nickName;

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoUserId);

        if (optionalUser.isEmpty()) {
            KakaoUserJoinDto kakaoUserJoinDto = new KakaoUserJoinDto(userInfo);
            userRepository.save(kakaoUserJoinDto.toEntity());
            optionalUser = userRepository.findByKakaoId(kakaoUserId);
        }

        User user = optionalUser.get();

        session.setAttribute(SessionConst.LOGIN_MEMBER, userInfo);
        session.setAttribute(SessionConst.KAKAO_ACCESS_TOKEN, kakaoAccessToken);

        return user;
    }

    public User join(UserJoinDto userJoinDto) {
        return userRepository.save(userJoinDto.toEntity());
    }

    public Optional<User> getUserByKakaoId(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v1/user/logout")
                        .build(true))
                .header("Authorization", (String) session.getAttribute(SessionConst.KAKAO_ACCESS_TOKEN))
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        session.invalidate();
    }

    public Long makeNewConnectId() {
        Long newConnectId;
        Optional<User> userOptional;
        do {
            newConnectId = random.nextLong(RANDOM_BOUND);
            userOptional = userRepository.findUserByRelationConnectId(newConnectId);
        } while (userOptional.isPresent());
        return newConnectId;
    }

    public Optional<User> getUserByConnectId(Long partnerConnectId) {
        return userRepository.findUserByRelationConnectId(partnerConnectId);
    }
}
