package faang.school.projectservice.dto.invitation;

public record InviteSentEvent(Long userId, Long receiverId, Long projectId) { }
