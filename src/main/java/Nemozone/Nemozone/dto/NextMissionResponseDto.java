package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.Mission;
import lombok.Getter;

@Getter
public class NextMissionResponseDto {

    private final Long nextMissionId;
    private final String nextMissionText;
    private final Long order;

    public NextMissionResponseDto(Mission mission) {
        this.nextMissionId = mission.getId();
        this.nextMissionText = mission.getMissionText();
        this.order = mission.getOrder();
    }

    public NextMissionResponseDto(Long id, String missionText, Long order) {
        this.nextMissionId = id;
        this.nextMissionText = missionText;
        this.order = order;
    }
}
