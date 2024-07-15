package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
@Controller
public class UserController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectURI;

    private final UserService userService;

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

        return "login";
    }

    @GetMapping("/login/kakao/callback")
    @Tag(name = "Kakao Login Callback")
    @Operation(summary = "카카오 로그인 콜백", description = "사용자가 로그인 정보 입력 후 카카오 서버에서 로그인 코드를 담아 콜백")
    public ResponseEntity<?> KakaoLoginCallback(@RequestParam("code") String code, HttpServletRequest request) {
        String accessToken = userService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(accessToken);

        userService.kakaoLogin(userInfo);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, userInfo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userInfo);
    }

    @GetMapping("/logout")
    @Tag(name = "Logout")
    @Operation(summary = "로그아웃", description = "세션에서 사용자 정보를 제거하여 로그아웃")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "redirect:/login/kakao";
    }

    @GetMapping("/login-check")
    @Tag(name = "Login Check")
    @Operation(summary = "로그인 체크", description = "로그인이 정상적으로 되었다면 OK 출력")
    public ResponseEntity<?> loginCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("OK");
    }
}
