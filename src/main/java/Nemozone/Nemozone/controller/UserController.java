package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.UserJoinDto;
import Nemozone.Nemozone.dto.UserJoinRequestDto;
import Nemozone.Nemozone.dto.UserResponseDto;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.RelationService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.SessionConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@RequestMapping("/api")
@Controller
public class UserController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectURI;

    private final UserService userService;
    private final RelationService relationService;

    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 합니다.")
    public String kakaoLogin(Model model) {
        String location =
                "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                        + clientId
                        + "&redirect_uri="
                        + redirectURI;
        model.addAttribute("location", location);

        return "redirect:"+location;
    }

    @GetMapping("/login/kakao/callback")
    @ApiResponse(responseCode = "200",
            description = "성공. 만약 nickname이 null이면 회원가입 필요",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
            }
    )
    @Operation(summary = "카카오 로그인 콜백", description = "사용자가 로그인 정보 입력 후 카카오 서버에서 로그인 코드를 담아 콜백")
    public ResponseEntity<?> KakaoLoginCallback(@RequestParam("code") String code, HttpServletRequest request) {
        String accessToken = userService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(accessToken);

        HttpSession session = request.getSession();

        User user = userService.kakaoLogin(userInfo, session, accessToken);

        UserResponseDto userResponseDto = new UserResponseDto(user);

        return ResponseEntity
                .status(HttpStatus.MULTIPLE_CHOICES)
                .location(URI.create("https://www.nemoz.one/auth/kakao"))
                .body(userResponseDto);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "세션에서 사용자 정보를 제거하여 로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        userService.logout(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/login-check")
    @Operation(summary = "로그인 체크", description = "로그인이 정상적으로 되었다면 OK 출력")
    public ResponseEntity<?> loginCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("OK");
    }

    @PostMapping("/join")
    @ApiResponse(responseCode = "201",
            description = "회원가입 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "404", description = "user 엔티티가 존재하지 않음")
    @ApiResponse(responseCode = "400", description = "이미 회원가입되어 있음")
    @Operation(summary = "회원가입",
            description = "카카오 로그인 결과 json에 null 값이 있다면 회원가입이 되지 않은 것이므로 회원가입 진행")
    public ResponseEntity<?> join(
            HttpServletRequest request,
            @RequestBody UserJoinRequestDto userJoinRequestDto ) {

        KakaoUserInfoResponseDto userInfoResponseDto = (KakaoUserInfoResponseDto) request.getSession(false).getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfoResponseDto.id);

        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("user 엔티티가 존재하지 않습니다.");

        User user = optionalUser.get();

        if (user.getNickname() != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 회원가입 되어 있습니다.");
        }

        Long newConnectId = userService.makeNewConnectId();
        UserJoinDto userJoinDto = new UserJoinDto(user, userJoinRequestDto.nickname(), userJoinRequestDto.startDate(), newConnectId);

        User joinUser = userService.join(userJoinDto);

        UserResponseDto userResponseDto = new UserResponseDto(joinUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponseDto);
    }

    @GetMapping("/my-info")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
            }
    )
    @Operation(summary = "나의 정보", description = "로그인한 사용자의 정보 조회")
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        UserResponseDto userResponseDto = new UserResponseDto(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponseDto);

    }
}
