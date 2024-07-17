package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Getter
public class UserJoinDto {

    private final User user;
    private final String nickname;
    private final Date relationStartDate;
    private final Long relationConnectId;
    private final Relation relation;

    public UserJoinDto(User user, String nickname, Date relationFirstDate, Long relationConnectId) {
        this.user = user;
        this.nickname = nickname;
        this.relationStartDate = relationFirstDate;
        this.relationConnectId = relationConnectId;

        relation = Relation.builder()
                .users(Arrays.asList(user))
                .startDate(relationStartDate)
                .build();
    }

    public User toEntity() {
        user.setRelationConnectId(relationConnectId);
        user.setNickname(nickname);
        user.setRelation(relation);

        return user;
    }
}
