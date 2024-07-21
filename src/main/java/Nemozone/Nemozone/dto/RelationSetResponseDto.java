package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class RelationSetResponseDto {

    private Long id;
    private Date startDate;
    private Long nextMissionOrder;

    public RelationSetResponseDto(Relation relation) {
        this.id = relation.getId();
        this.startDate = relation.getStartDate();
        this.nextMissionOrder = relation.getNextMissionOrder();
    }
}
