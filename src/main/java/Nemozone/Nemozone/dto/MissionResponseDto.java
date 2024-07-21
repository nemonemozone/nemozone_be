package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import lombok.Getter;

import java.util.Date;

@Getter
public class MissionResponseDto {

    private Long missionId;
    private String missionText;
    private Long order;
    private String s3Url1;
    private String s3Url2;
    private Date createdAt;
    private String userNickname1;
    private String userNickname2;
    private Long day;

    public MissionResponseDto(Mission mission, Relation relation, Photo photo) {
        this.missionId = mission.getId();
        this.missionText = mission.getMissionText();
        this.order = mission.getOrder();
        this.s3Url1 = photo.getS3Url1();
        this.s3Url2 = photo.getS3Url2();
        this.createdAt = photo.getCreatedAt();
        this.userNickname1 = relation.getUsers().get(0).getNickname();
        if (relation.getUsers().size() > 1) {
            this.userNickname2 = relation.getUsers().get(1).getNickname();
        }
        this.day = photo.getDay();
    }

    public MissionResponseDto(Long missionId, String missionText, Long order, String s3Url1, String s3Url2, Date createdAt, String userNickname1, String userNickname2, Long day) {
        this.missionId = missionId;
        this.missionText = missionText;
        this.order = order;
        this.s3Url1 = s3Url1;
        this.s3Url2 = s3Url2;
        this.createdAt = createdAt;
        this.userNickname1 = userNickname1;
        this.userNickname2 = userNickname2;
        this.day = day;
    }
}
