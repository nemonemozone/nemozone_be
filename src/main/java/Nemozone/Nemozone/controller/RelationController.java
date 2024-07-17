package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.PartnerInfoResponseDto;
import Nemozone.Nemozone.dto.RelationInfoResponseDto;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.RelationService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.SessionConst;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/relation")
@Controller
public class RelationController {

    private final UserService userService;
    private final RelationService relationService;

    @GetMapping("")
    public ResponseEntity<?> getRelationInfo(HttpServletRequest request) {
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());

        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("user 엔티티가 없습니다.");

        User user = optionalUser.get();
        RelationInfoResponseDto relationInfo = relationService.getRelationInfo(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(relationInfo);
    }

    @PostMapping("")
    public ResponseEntity<?> setRelation (
            //HttpServletRequest request,
            //@RequestParam(required = false, value = "partnerConnectIdString") Long partnerConnectId,
            HttpServletRequest request
            ) throws IOException {
        //Long partnerConnectId = Long.getLong(partnerConnectIdString);
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messageBody={}", messageBody);

        String[] split = messageBody.split("\\{|\"|:|}");
        log.info(split[2]);
        log.info(split[4]);
        Long partnerConnectId = Long.parseLong(split[4]);
        HttpSession session = request.getSession();
        KakaoUserInfoResponseDto userInfoResponseDto = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long userKakaoId = userInfoResponseDto.getId();
        Optional<User> userOptional = userService.getUserByKakaoId(userKakaoId);

        if (userOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("user 엔티티가 존재하지 않습니다.");

        User user = userOptional.get();

        if (user.getRelation().getUsers().size() != 1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 커플인 유저입니다.");
        }

        Optional<User> optionalPartner = userService.getUserByConnectId(partnerConnectId);

        if (optionalPartner.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파트너 user 엔티티가 존재하지 않습니다.");

        User partner = optionalPartner.get();

        if (partner.getRelation().getUsers().size() != 1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 커플인 파트너입니다.");
        }

        Relation relation = relationService.setNewRelation(user, partner);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(relation);
    }

    @GetMapping("/partner")
    public ResponseEntity<?> getPartnerInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        PartnerInfoResponseDto partnerInfoResponseDto = relationService.getPartnerInfo(user);
        HttpStatus responseHttpStatus = HttpStatus.OK;
        if (partnerInfoResponseDto.getPartnerId() == -1L)
            responseHttpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(responseHttpStatus)
                .body(partnerInfoResponseDto);
    }

    @GetMapping("/date")
    public ResponseEntity<?> getRelationTotalDate(HttpServletRequest request) {
        HttpSession session = request.getSession();
        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());

        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("user 엔티티가 존재하지 않습니다.");

        User user = optionalUser.get();

        Long totalDate = relationService.getTotalDate(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TotalDateWrapper(totalDate));
    }

    private static class TotalDateWrapper {
        private Long totalDate;

        public TotalDateWrapper(Long totalDate) {
            this.totalDate = totalDate;
        }

        public Long getTotalDate() {
            return totalDate;
        }
    }
}
