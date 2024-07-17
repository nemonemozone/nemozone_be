package Nemozone.Nemozone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    @ManyToOne
    @Column(name = "relation_id")
    private Relation relation;

    @Column(name = "image_s3_url")
    private String s3Url;

    @Column
    private Long day;

    @ManyToOne
    @Column
    private Mission mission;

    @Builder
    public Photo(Long id, Relation relation, String s3Url, Long day, Mission mission) {
        this.id = id;
        this.relation = relation;
        this.s3Url = s3Url;
        this.day = day;
        this.mission = mission;
    }
}
