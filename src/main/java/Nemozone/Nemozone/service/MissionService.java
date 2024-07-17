package Nemozone.Nemozone.service;

import Nemozone.Nemozone.entity.Mission;
import Nemozone.Nemozone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public Optional<Mission> getMissionByDay(Long day) {


    }
}
