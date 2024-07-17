package Nemozone.Nemozone.dto;

import Nemozone.Nemozone.entity.User;
import lombok.Getter;

@Getter
public class PartnerInfoResponseDto {
    private Long relationId;
    private Long partnerId;
    private Long partnerKakaoId;
    private String partnerName;
    private String partnerNickname;

    public PartnerInfoResponseDto(User user) {
        this.relationId = user.getRelation().getId();
        this.partnerId = user.getUserId();
        this.partnerKakaoId = user.getKakaoId();
        this.partnerName = user.getKakaoNickname();
        this.partnerNickname = user.getNickname();
    }

    public PartnerInfoResponseDto(Long relationId, Long partnerId, Long partnerKakaoId, String partnerName, String partnerNickname) {
        this.relationId = relationId;
        this.partnerId = partnerId;
        this.partnerKakaoId = partnerKakaoId;
        this.partnerName = partnerName;
        this.partnerNickname = partnerNickname;
    }
}
