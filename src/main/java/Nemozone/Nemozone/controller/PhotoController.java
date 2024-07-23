package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.PhotoResponseDto;
import Nemozone.Nemozone.dto.PhotoSaveRequestDto;
import Nemozone.Nemozone.dto.PhotoSaveResponseDto;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.PhotoService;
import Nemozone.Nemozone.service.RelationService;
import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.KakaoTokenConst;
import Nemozone.Nemozone.session.SessionConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Photo", description = "Photo API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/photos")
@Controller
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;
    private final RelationService relationService;

    @Operation(summary = "모든 사진 조회", description = "로그인한 사용자의 모든 사진 조회")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = PhotoResponseDto.class))
                )
            })
    @ApiResponse(responseCode = "404", description = "조회 실패")
    @GetMapping("")
    public ResponseEntity<?> getAllPhoto(HttpServletRequest request) throws Exception {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        List<PhotoResponseDto> photoResponseDtos;
        try {
            photoResponseDtos = photoService.getPhotosByKakaoId(userInfo.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ArrayList());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(photoResponseDtos);
    }

    @Operation(summary = "사진 저장", description = "사진 저장")
    @ApiResponse(responseCode = "201",
            description = "저장 성공",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PhotoSaveResponseDto.class)
                )
    })
    @PostMapping("")
    public ResponseEntity<?> savePhoto(@RequestBody PhotoSaveRequestDto requestDto, HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        PhotoSaveResponseDto responseDto = photoService.savePhoto(requestDto, optionalUser.get());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @Operation(summary = "사진 ID로 조회", description = "사진 ID로 사진 조회")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사진이 존재하지 않음")
    @ApiResponse(responseCode = "400", description = "접근 권한이 없음")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PhotoResponseDto.class))})
    @GetMapping("/{photo_id}")
    public ResponseEntity<?> getPhotoById(@PathVariable("photo_id") Long photoId, HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        Optional<Photo> optionalPhoto = photoService.getPhotoByPhotoId(photoId);
        if (optionalPhoto.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        Photo photo = optionalPhoto.get();

        if (!photo.getRelation().getUsers().contains(user)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        PhotoResponseDto responseDto = PhotoResponseDto.makeByPhoto(photo);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
