package Nemozone.Nemozone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_relation")
public class Relation {

    @GeneratedValue
    @Id
    @Column(name = "relation_id")
    private Long id;

    @JsonIgnoreProperties({"relation"})
    @OneToMany(mappedBy = "relation", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @Column
    private List<User> users;

    @Column(name = "start_date")
    private Date startDate;

    @Setter
    @Column(name = "next_mission_order")
    private Long nextMissionOrder;

    @OneToMany(mappedBy = "relation", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnoreProperties({"relation"})
    private List<Photo> photos;

    public void addUser(User user) {
        users.add(user);
    }

    public void plusOneNextMissionOrder() {
        this.nextMissionOrder++;
    }

    @Builder
    public Relation(Long id, List<User> users, Date startDate, Long nextMissionOrder, List<Photo> photos) {
        this.id = id;
        this.users = users;
        this.startDate = startDate;
        this.nextMissionOrder = nextMissionOrder;
        this.photos = photos;
    }
}
