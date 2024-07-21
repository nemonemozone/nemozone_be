package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class PhotoSaveResponseDto {

    private Long id;
    private Relation relation;
    private String s3Url1;
    private String s3Url2;
    private Long day;
    private Mission mission;

    @Builder
    public PhotoSaveResponseDto(Long id, Relation relation, String s3Url1, String s3Url2, Long day, Mission mission) {
        this.id = id;
        this.relation = relation;
        this.s3Url1 = s3Url1;
        this.s3Url2 = s3Url2;
        this.day = day;
        this.mission = mission;
    }

    public PhotoSaveResponseDto(Photo photo) {
        this.id = photo.getId();
        this.day = photo.getDay();
        this.mission = photo.getMission();
        this.relation = photo.getRelation();
        this.s3Url1 = photo.getS3Url1();
        this.s3Url2 = photo.getS3Url2();
    }
}
