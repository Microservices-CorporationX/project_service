package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Meeting API", description = "Endpoints for meetings")
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping("/create")
    @Operation(summary = "Create a new meeting")
    public ResponseEntity<MeetDto> createMeeting(@Valid @RequestBody MeetDto createMeetDto) {
        log.info("Creating meeting with id {} and creator id {}", createMeetDto.getId(), createMeetDto.getCreatorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.createMeeting(createMeetDto));
    }

    @PostMapping("/update/{meetId}")
    @Operation(summary = "Update a meeting by providing the meeting ID.")
    public ResponseEntity<MeetDto> updateMeeting(@Valid @RequestBody MeetDto updateMeetDto,
                                                 @PathVariable @Positive long meetId) {
        log.info("Updating meeting with id {} and creator id {}", updateMeetDto.getId(), updateMeetDto.getCreatorId());
        return ResponseEntity.ok(meetingService.updateMeeting(updateMeetDto, meetId));
    }

    @DeleteMapping("/delete/{projectId}/{currentUserId}/{meetId}")
    @Operation(summary = "Delete a meeting, only if the user is the project owner")
    public ResponseEntity<Void> deleteMeeting(@PathVariable @Positive Long projectId,
                                              @PathVariable @Positive Long currentUserId,
                                              @PathVariable @Positive long meetId) {
        log.info("Deleting meeting with id {} by user {}", meetId, currentUserId);
        meetingService.deleteMeeting(projectId, currentUserId, meetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filters")
    @Operation(summary = "Filter meetings based on provided criteria")
    public ResponseEntity<List<MeetDto>> filterMeetings(@RequestBody MeetDto filter) {
        log.info("Filtering meetings with criteria {}", filter);
        return ResponseEntity.ok(meetingService.filterMeetings(filter));
    }

    @GetMapping
    @Operation(summary = "Get all meetings")
    public ResponseEntity<List<MeetDto>> getAllMeetings() {
        log.info("Getting all meetings");
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/meeting/{meetId}")
    @Operation(summary = "Get a meeting by its ID")
    public ResponseEntity<MeetDto> getMeetingById(@PathVariable @Positive long meetId) {
        log.info("Getting meeting with id {}", meetId);
        return ResponseEntity.ok(meetingService.getMeetingById(meetId));
    }
}
