package Nemozone.Nemozone.controller;

import Nemozone.Nemozone.dto.KakaoUserInfoResponseDto;
import Nemozone.Nemozone.dto.MissionResponseDto;
import Nemozone.Nemozone.dto.NextMissionResponseDto;
import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.service.MissionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Tag(name = "Mission", description = "Mission API")
@RequiredArgsConstructor
@RequestMapping("/api/missions")
@Controller
public class MissionController {

    private final UserService userService;
    private final RelationService relationService;
    private final MissionService missionService;

    @Operation(summary = "다음 미션 정보 조회", description = "로그인한 사용자의 다음 미션 정보 조회")
    @ApiResponse(responseCode = "400", description = "커플이 아님")
    @ApiResponse(responseCode = "404", description = "다음 미션이 존재하지 않음")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = NextMissionResponseDto.class)
                )
            }
    )
    @GetMapping("/next")
    public ResponseEntity<?> getNextMission(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));


        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();

        Optional<Relation> optionalRelation = relationService.getRelationByUser(user);
        if (optionalRelation.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        Relation relation = optionalRelation.get();

        Optional<Mission> optionalMission = missionService.getMissionByOrder(relation.getNextMissionOrder());
        if (optionalMission.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();

        Mission mission = optionalMission.get();

        NextMissionResponseDto missionResponseDto = new NextMissionResponseDto(mission);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionResponseDto);
    }

    @Operation(summary = "전체 미션 조회", description = "로그인한 사용자가 파트너와 수행한 전체 미션 조회")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema =@Schema(implementation = MissionResponseDto.class))
                )})
    @ApiResponse(responseCode = "400", description = "커플이 아님")
    @GetMapping("")
    public ResponseEntity<?> getAllMission(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        Optional<Relation> optionalRelation = relationService.getRelationByUser(user);
        if (optionalRelation.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        Relation relation = optionalRelation.get();
        List<MissionResponseDto> missionResponseDtos = missionService.getMissionsByRelation(relation);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionResponseDtos);
    }

    @Operation(summary = "미션 ID로 수행한 미션 정보 조회", description = "로그인한 사용자가 파트너와 수행한 미션 정보를 ID로 조회")
    @ApiResponse(responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MissionResponseDto.class)
            )
            }
    )
    @ApiResponse(responseCode = "400", description = "커플이 아님")
    @ApiResponse(responseCode = "404", description = "정보가 없음")
    @GetMapping("/{mission_id}")
    public ResponseEntity<?> getMissionById(@PathVariable("mission_id") Long missionId, HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        KakaoUserInfoResponseDto userInfo = (KakaoUserInfoResponseDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
        KakaoUserInfoResponseDto userInfo = userService.getUserInfo(request.getHeader(KakaoTokenConst.HEADER));

        Optional<User> optionalUser = userService.getUserByKakaoId(userInfo.getId());
        User user = optionalUser.get();
        Optional<Relation> optionalRelation = relationService.getRelationByUser(user);
        if (optionalRelation.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        Relation relation = optionalRelation.get();
        MissionResponseDto missionResponseDto;
        try {
            missionResponseDto = missionService.getMissionById(missionId, relation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionResponseDto);
    }
}
