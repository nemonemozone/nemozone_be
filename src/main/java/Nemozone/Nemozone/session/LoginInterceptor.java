package Nemozone.Nemozone.session;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    public final UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader(KakaoTokenConst.HEADER);
        KakaoUserInfoResponseDto userInfo;
        try {
            userInfo = userService.getUserInfo(accessToken);
        } catch (Exception e) {
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().println("로그인이 필요합니다.");
            return false;
        }
        //KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
//        if (userInfo == null) {
//            response.setContentType("text/html; charset=utf-8");
//            response.getWriter().println("로그인이 필요합니다.");
//            return false;
//        } else {
//            return true;
//        }
        return true;
    }
}
