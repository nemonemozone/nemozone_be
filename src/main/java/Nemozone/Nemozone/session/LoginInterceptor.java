package Nemozone.Nemozone.session;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        if (userInfo == null) {
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().println("잘못된 접근입니다.");
            return false;
        } else {
            return true;
        }
    }
}
