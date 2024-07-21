package Nemozone.Nemozone.service;

import Nemozone.Nemozone.dto.PhotoResponseDto;
import Nemozone.Nemozone.dto.PhotoSaveRequestDto;
import Nemozone.Nemozone.dto.PhotoSaveResponseDto;
import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.repository.PhotoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PhotoService {

    private final RelationService relationService;
    private final MissionService missionService;
    private final PhotoRepository photoRepository;
    private final UserService userService;

    public List<PhotoResponseDto> getPhotosByKakaoId(Long kakaoId) throws Exception {
        Optional<User> optionalUser = userService.getUserByKakaoId(kakaoId);
        User user = optionalUser.get();
        Optional<Relation> optionalRelation = relationService.getRelationByUser(user);
        if (optionalRelation.isEmpty())
            throw new Exception();
        Relation relation = optionalRelation.get();
        List<Photo> photoByRelation = photoRepository.findPhotoByRelation(relation);
        List<PhotoResponseDto> responseDtoList = new ArrayList<>();
        for (Photo photo : photoByRelation) {
            PhotoResponseDto responseDto = PhotoResponseDto.makeByPhoto(photo);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    public PhotoSaveResponseDto savePhoto(PhotoSaveRequestDto requestDto, User user) {
        Optional<Relation> optionalRelation = relationService.getRelationByUser(user);
        Relation relation = optionalRelation.get();
        Long nextMissionOrder = relation.getNextMissionOrder();
        relation.plusOneNextMissionOrder();

        Optional<Mission> optionalMission = missionService.getMissionByOrder(nextMissionOrder);
        Mission mission = optionalMission.get();

        requestDto.setDay(relationService.getTotalDate(user));
        requestDto.setRelation(relation);
        requestDto.setMission(mission);


        Photo savePhotoEntity = photoRepository.save(requestDto.toEntity());
        return new PhotoSaveResponseDto(savePhotoEntity);
    }

    public Optional<Photo> getPhotoByPhotoId(Long photoId) {
        return photoRepository.findPhotoById(photoId);
    }
}
