package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.*;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.RelationService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.KakaoTokenConst;
import Nemozone.Nemozone.session.SessionConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Tag(name = "Relation", description = "Relation API")
@RequiredArgsConstructor
@RequestMapping("/api/relation")
@Controller
public class RelationController {

    private final UserService userService;
    private final RelationService relationService;

    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RelationInfoResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "404", description = "user 엔티티가 존재하지 않음")
    @Operation(summary = "로그인한 커플의 모든 정보 조회", description = "로그인한 유저가 속한 커플의 모든 정보를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<?> getRelationInfo(HttpServletRequest request) throws Exception {
        //KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));
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

    @Operation(summary = "커플 연결", description = "로그인한 유저가 파트너 연결 ID로 커플 연결")
    @ApiResponse(responseCode = "200",
            description = "커플 연결 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RelationSetResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "404", description = "user 엔티티가 존재하지 않음")
    @ApiResponse(responseCode = "400", description = "이미 커플인 유저")
    @PostMapping("")
    public ResponseEntity<?> setRelation (
            @RequestBody RelationSetDto relationSetDto,
            HttpServletRequest request) throws Exception {

        Long partnerConnectId = relationSetDto.partnerConnectId();
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfoResponseDto = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfoResponseDto = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));
        Long userKakaoId = userInfoResponseDto.getId();
        Optional<User> userOptional = userService.getUserByKakaoId(userKakaoId);

        if (userOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
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
                    .status(HttpStatus.NOT_FOUND)
                    .body("파트너 user 엔티티가 존재하지 않습니다.");

        User partner = optionalPartner.get();

        if (partner.getRelation().getUsers().size() != 1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 커플인 파트너입니다.");
        }

        Relation relation = relationService.setNewRelation(user, partner);
        RelationSetResponseDto relationSetResponseDto = new RelationSetResponseDto(relation);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(relationSetResponseDto);
    }

    @Operation(summary = "파트너 정보", description = "로그인한 유저의 파트너 정보 조회")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PartnerInfoResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "404", description = "파트너 정보가 존재하지 않음")
    @GetMapping("/partner")
    public ResponseEntity<?> getPartnerInfo(HttpServletRequest request) throws Exception {

//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);

        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));


        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        PartnerInfoResponseDto partnerInfoResponseDto = relationService.getPartnerInfo(user);
        HttpStatus responseHttpStatus = HttpStatus.OK;

        if (partnerInfoResponseDto.getPartnerId() == -1L)
            responseHttpStatus = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(responseHttpStatus)
                .body(partnerInfoResponseDto);
    }

    @Operation(summary = "사귄 기간 조회", description = "로그인한 유저가 파트너와 사귄 기간 조회")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TotalDateResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "404", description = "user 엔티티가 존재하지 않음")
    @GetMapping("/date")
    public ResponseEntity<?> getRelationTotalDate(HttpServletRequest request) throws Exception {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());

        if (optionalUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("user 엔티티가 존재하지 않습니다.");

        User user = optionalUser.get();

        Long totalDate = relationService.getTotalDate(user);
        TotalDateResponseDto totalDateResponseDto = new TotalDateResponseDto(totalDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(totalDateResponseDto);
    }
}
