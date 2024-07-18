package Nemozone.Nemozone.service;

import Nemozone.Nemozone.dto.PartnerInfoResponseDto;
import Nemozone.Nemozone.dto.RelationInfoResponseDto;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import Nemozone.Nemozone.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RelationService {
    private final RelationRepository relationRepository;

    public Optional<Relation> getRelationByUser(User user) {
        return relationRepository.findRelationByUsersContains(user);
    }

    public Relation setNewRelation(User user, User partner) {
        Relation mainRelation = user.getRelation();
        Relation sideRelation = partner.getRelation();

        partner.setRelation(mainRelation);
        mainRelation.getUsers().add(partner);

        relationRepository.delete(sideRelation);

        return mainRelation;
    }

    public RelationInfoResponseDto getRelationInfo(User user) {
        Optional<Relation> optionalRelation = relationRepository.findRelationByUsersContains(user);
        Relation relation = optionalRelation.get();
        List<User> users = relation.getUsers();

        if (users.size() == 1)
            return new RelationInfoResponseDto(relation.getId(), users.get(0), null,
                    relation.getStartDate(), getTotalDate(user));
        else
            return new RelationInfoResponseDto(relation.getId(), users.get(0), users.get(1),
                    relation.getStartDate(), getTotalDate(user));
    }

    public Long getTotalDate(User user) {
        Optional<Relation> optionalRelation = getRelationByUser(user);

        Relation relation = optionalRelation.get();
        Date startDate = relation.getStartDate();
        Date now = new Date();

        Calendar startDayCalendar = Calendar.getInstance();
        Calendar nowCalendar = Calendar.getInstance();

        startDayCalendar.setTime(startDate);
        nowCalendar.setTime(now);

        Long totalDate = (nowCalendar.getTimeInMillis() - startDayCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24L) + 1;

        return totalDate;
    }

    public PartnerInfoResponseDto getPartnerInfo(User user) {
        Optional<Relation> optionalRelation = relationRepository.findRelationByUsersContains(user);
        Relation relation = optionalRelation.get();

        for (User u : relation.getUsers()) {
            if (u.getUserId() != user.getUserId()) {
                return new PartnerInfoResponseDto(u);
            }
        }
        return new PartnerInfoResponseDto(-1L, -1L, -1L,
                "파트너가 존재하지 않습니다.", "파트너가 존재하지 않습니다.");
    }
}
