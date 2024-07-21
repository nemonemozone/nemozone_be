package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PhotoSaveRequestDto {

    @JsonIgnore
    private Relation relation;
    private String s3Url1;
    private String s3Url2;
    @JsonIgnore
    private Long day;
    @JsonIgnore
    private Mission mission;

    @Builder
    public PhotoSaveRequestDto(Relation relation, String s3Url1, String s3Url2, Long day, Mission mission) {
        this.relation = relation;
        this.s3Url1 = s3Url1;
        this.s3Url2 = s3Url2;
        this.day = day;
        this.mission = mission;
    }


    public Photo toEntity() {
        return Photo.builder()
                .day(day)
                .mission(mission)
                .relation(relation)
                .s3Url1(s3Url1)
                .s3Url2(s3Url2)
                .build();
    }
}
