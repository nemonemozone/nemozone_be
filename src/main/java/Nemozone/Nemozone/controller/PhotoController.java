package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.PhotoService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/photos")
@Controller
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;

    @GetMapping("")
    public String getAllPhoto(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return "file";
    }

    @PostMapping("")
    public ResponseEntity<?> savePhoto(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        photoService.savePhoto(file, session, optionalUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
