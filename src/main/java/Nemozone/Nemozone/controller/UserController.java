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
@Tag(name = "User API", description = "User API 입니다")
@Controller
public class UserController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectURI;

    private final UserService userService;

    @GetMapping("/login/kakao")
    @Tag(name = "Kakao Login API")
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
    public ResponseEntity<?> KakaoLoginCallback(@RequestParam("code") String code, HttpServletRequest request) {
        String accessToken = userService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(accessToken);

        userService.kakaoLogin(userInfo);

        Long kakaoUserId = userInfo.id;
        String nickname = userInfo.kakaoAccount.profile.nickName;

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, userInfo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userInfo);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "redirect:/login/kakao";
    }

    @GetMapping("/login-check")
    public ResponseEntity<?> loginCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("OK");
    }
}
