package Nemozone.Nemozone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void addUser(User user) {
        users.add(user);
    }

    @Builder
    public Relation(Long id, List<User> users, Date startDate) {
        this.id = id;
        this.users = users;
        this.startDate = startDate;
    }
}
