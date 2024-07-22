package Nemozone.Nemozone.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class SessionController {

    @GetMapping("/setSession")
    public String setSession(@RequestParam String sessionId, HttpServletResponse response) {
        // 세션 ID를 쿠키로 설정
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS를 사용하는 경우
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효기간 설정 (7일)

        response.addCookie(cookie);
        return "Session ID set in cookie";
    }
}
