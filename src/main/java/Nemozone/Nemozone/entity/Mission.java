package Nemozone.Nemozone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    @Column(name = "mission_text")
    private String missionText;

    @Column(name = "mission_order")
    private Long order;

    @OneToMany(mappedBy = "mission")
    @Column
    private List<Photo> photos;

    @Builder
    public Mission(Long id, String missionText, Long order, List<Photo> photos) {
        this.id = id;
        this.missionText = missionText;
        this.order = order;
        this.photos = photos;
    }
}
