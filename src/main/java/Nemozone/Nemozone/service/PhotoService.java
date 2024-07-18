package Nemozone.Nemozone.service;

import Nemozone.Nemozone.dto.PhotoSaveRequestDto;
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
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PhotoService {

    private final RelationService relationService;
    private final MissionService missionService;
    private final PhotoRepository photoRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void savePhoto(MultipartFile multipartFile, HttpSession session, Optional<User> optionalUser) throws IOException {
        String s3Url = savePhoto(multipartFile);

        User user = optionalUser.get();
        Relation relation = user.getRelation();
        Long totalDate = relationService.getTotalDate(user);
        Optional<Mission> optionalMission = missionService.getMissionByOrder(relation.getNextMissionOrder());
        Mission mission = optionalMission.get();
        PhotoSaveRequestDto photoSaveRequestDto = new PhotoSaveRequestDto(relation, s3Url, totalDate, mission);
        photoRepository.save(photoSaveRequestDto.toEntity());
        relation.plusOneNextMissionOrder();
    }

    public String savePhoto(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }
}
