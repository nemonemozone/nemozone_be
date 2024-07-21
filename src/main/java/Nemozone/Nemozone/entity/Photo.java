package Nemozone.Nemozone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "photo_table")
public class Photo {

    @Id
    @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relation_id")
    private Relation relation;

    @Column(name = "image_s3_url1")
    private String s3Url1;

    @Column(name = "image_s3_url2")
    private String s3Url2;

    @Column(name = "relation_day")
    private Long day;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @Builder
    public Photo(Long id, Relation relation, String s3Url1, String s3Url2, Long day, Mission mission) {
        this.id = id;
        this.relation = relation;
        this.s3Url1 = s3Url1;
        this.s3Url2 = s3Url2;
        this.day = day;
        this.mission = mission;
    }
}
