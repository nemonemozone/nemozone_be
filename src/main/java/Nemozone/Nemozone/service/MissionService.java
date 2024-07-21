package Nemozone.Nemozone.service;

import Nemozone.Nemozone.dto.MissionResponseDto;
import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public Optional<Mission> getMissionByOrder(Long order) {

        return missionRepository.findMissionByOrder(order);

    }

    public List<MissionResponseDto> getMissionsByRelation(Relation relation) {
        ArrayList<MissionResponseDto> missionResponseDtos = new ArrayList<>();
        List<Photo> photos = relation.getPhotos();
        for (Photo photo : photos) {
            Mission mission = photo.getMission();
            missionResponseDtos.add(new MissionResponseDto(mission, relation, photo));
        }
        return missionResponseDtos;
    }

    public MissionResponseDto getMissionById(Long missionId, Relation relation) throws Exception {
        List<Photo> photos = relation.getPhotos();
        for (Photo photo : photos) {
            if (photo.getMission().getId() != missionId)
                continue;
            Mission mission = photo.getMission();
            MissionResponseDto missionResponseDto = new MissionResponseDto(mission, relation, photo);
            return missionResponseDto;
        }
        throw new Exception();
    }
}
