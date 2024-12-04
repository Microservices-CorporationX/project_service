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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/meeting")
    @Operation(summary = "Create a new meeting")
    public ResponseEntity<MeetDto> createMeeting(@Valid @RequestBody MeetDto createMeetDto) {
        log.info("Creating meeting with id {} and creator id {}", createMeetDto.getId(), createMeetDto.getCreatorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.createMeeting(createMeetDto));
    }

    @PutMapping("/{meetId}")
    @Operation(summary = "Update a meeting by providing the meeting ID.")
    public ResponseEntity<MeetDto> updateMeeting(
            @PathVariable
            @Positive(message = "Meeting ID must be a positive number") Long meetId,
            @Valid @RequestBody MeetDto updateMeetDto) {
        log.info("Updating meeting with id {} and creator id {}", updateMeetDto.getId(), updateMeetDto.getCreatorId());
        return ResponseEntity.ok(meetingService.updateMeeting(updateMeetDto, meetId));
    }

    @DeleteMapping("/meetings/{meetId}/creators/{creatorId}")
    @Operation(summary = "Delete a meeting by its ID by owning user")
    public ResponseEntity<Void> deleteMeeting(
            @PathVariable @Positive(message = "Meeting ID must be a positive number") long meetId,
            @PathVariable @Positive(message = "Creator ID must be a positive number") long creatorId) {
        log.info("Deleting meeting with id {}", meetId);
        meetingService.deleteMeeting(meetId, creatorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filters")
    @Operation(summary = "Get meetings based on provided criteria")
    @RequestMapping("/filters")
    public ResponseEntity<List<MeetDto>> filterMeetings(MeetDto meetDto) {
        log.info("Filtering meetings based on provided criteria {} ", meetDto);
        return ResponseEntity.ok(meetingService.filterMeetings(meetDto));
    }

    @GetMapping("/meetings")
    @Operation(summary = "Get all meetings")
    public ResponseEntity<List<MeetDto>> getAllMeetings() {
        log.info("Getting all meetings");
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/meetings/{meetId}")
    @Operation(summary = "Get a meeting by its ID")
    public ResponseEntity<MeetDto> getMeetingById(
            @PathVariable
            @Positive(message = "Meeting ID must be a positive number")
            Long meetId
    ) {
        log.info("Getting meeting with id {}", meetId);
        return ResponseEntity.ok(meetingService.getMeetingById(meetId));
    }
}
