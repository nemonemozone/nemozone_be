package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import lombok.Getter;

@Getter
public class PhotoSaveRequestDto {

    private Relation relation;
    private String s3Url;
    private Long day;
    private Mission mission;

    public PhotoSaveRequestDto(Relation relation, String s3Url, Long day, Mission mission) {
        this.relation = relation;
        this.s3Url = s3Url;
        this.day = day;
        this.mission = mission;
    }

    public Photo toEntity() {
        return Photo.builder()
                .day(day)
                .mission(mission)
                .relation(relation)
                .s3Url(s3Url)
                .build();
    }
}
