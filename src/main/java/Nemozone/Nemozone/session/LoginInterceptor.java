package Nemozone.Nemozone.session;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.service.UserService;
import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

import static org.springframework.http.HttpHeaders.ORIGIN;

@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    public final UserService userService;


    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptionsMethod(request);
    }

    private boolean isOptionsMethod(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPreflightRequest(request)) {
            return true;
        }


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
