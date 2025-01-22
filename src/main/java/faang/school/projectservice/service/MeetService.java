package faang.school.projectservice.service;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;

    public List<Meet> getMeetsByIds(List<Long> meetIds) {
        return meetRepository.findAllById(meetIds);
    }
}
