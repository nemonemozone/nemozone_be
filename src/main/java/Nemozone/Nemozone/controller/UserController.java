package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.UserJoinDto;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.RelationService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.SessionConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
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
    @Tag(name = "Kakao Login")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 합니다.")
    public String kakaoLogin(Model model) {
        String location =
                "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                        + clientId
                        + "&redirect_uri="
                        + redirectURI;
        model.addAttribute("location", location);

        //return "login";
        return "redirect:"+location;
    }

    @GetMapping("/login/kakao/callback")
    @Tag(name = "Kakao Login Callback")
    @Operation(summary = "카카오 로그인 콜백", description = "사용자가 로그인 정보 입력 후 카카오 서버에서 로그인 코드를 담아 콜백")
    public String KakaoLoginCallback(@RequestParam("code") String code, HttpServletRequest request) {
        String accessToken = userService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(accessToken);

        HttpSession session = request.getSession();

        userService.kakaoLogin(userInfo, session, accessToken);

//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("OK");

        return "join";
    }

    @GetMapping("/logout")
    @Tag(name = "Logout")
    @Operation(summary = "로그아웃", description = "세션에서 사용자 정보를 제거하여 로그아웃")
    public String logout(HttpServletRequest request) {
        userService.logout(request);

        return "redirect:/api/login/kakao";
    }

    @GetMapping("/login-check")
    @Tag(name = "Login Check")
    @Operation(summary = "로그인 체크", description = "로그인이 정상적으로 되었다면 OK 출력")
    public ResponseEntity<?> loginCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(
            HttpServletRequest request,
            @RequestParam(value = "nickname") String nickname,
            @RequestParam(value = "startDate") Date relationFirstDate) {

        KakaoUserInfoResponseDto userInfoResponseDto = (KakaoUserInfoResponseDto) request.getSession(false).getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfoResponseDto.id);

        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("user 엔티티가 존재하지 않습니다.");

        User user = optionalUser.get();

        if (user.getNickname() != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 회원가입 되어 있습니다.");
        }

        Long newConnectId = userService.makeNewConnectId();
        UserJoinDto userJoinDto = new UserJoinDto(user, nickname, relationFirstDate, newConnectId);

        userService.join(userJoinDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userJoinDto.getUser());
    }
}
