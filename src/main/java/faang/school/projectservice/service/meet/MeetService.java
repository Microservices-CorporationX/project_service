package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;

    public MeetDto createMeeting(MeetDto meetDto) {
        log.info("");
        return ;
    }

    public MeetDto updateMeeting(MeetDto meetDto) {
        log.info("");
        return ;
    }

    public void deleteMeeting(Long meetId) {
        log.info("");
    }

    public List<MeetDto> getMeetingsByProjectFilteredByDateOrTitle(Long projectId, String title, LocalDateTime date) {
        log.info("");
        return ;
    }

    public List<MeetDto> getAllMeetings() {
        log.info("");
        return ;
    }

    public MeetDto getMeetingById(Long meetId) {
        log.info("");
        return ;
    }
}
