package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PhotoResponseDto {

    private Long id;
    private String s3Url1;
    private String s3Url2;
    private Long missionId;
    private String missionText;
    private Long missionOrder;
    private Date missionDate;
    private String userNickname1;
    private String userNickname2;

    public static PhotoResponseDto makeByPhoto(Photo photo) {
        Relation relation = photo.getRelation();
        PhotoResponseDto responseDto = PhotoResponseDto.builder()
                .id(photo.getId())
                .s3Url1(photo.getS3Url1())
                .s3Url2(photo.getS3Url2())
                .missionId(photo.getMission().getId())
                .missionText(photo.getMission().getMissionText())
                .missionDate(photo.getCreatedAt())
                .missionOrder(photo.getDay())
                .userNickname1(relation.getUsers().get(0).getNickname())
                .build();
        if (relation.getUsers().size() > 1) {
            responseDto.setUserNickname2(relation.getUsers().get(1).getNickname());
        }
        return responseDto;
    }

    @Builder
    public PhotoResponseDto(Long id, String s3Url1, String s3Url2, Long missionId, String missionText, Long missionOrder, Date missionDate, String userNickname1, String userNickname2) {
        this.id = id;
        this.s3Url1 = s3Url1;
        this.s3Url2 = s3Url2;
        this.missionId = missionId;
        this.missionText = missionText;
        this.missionOrder = missionOrder;
        this.missionDate = missionDate;
        this.userNickname1 = userNickname1;
        this.userNickname2 = userNickname2;
    }
}
