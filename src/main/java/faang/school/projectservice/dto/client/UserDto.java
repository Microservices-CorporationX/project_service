package faang.school.projectservice.dto.client;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;

    public boolean isSameUser(Long userId) {
        return this.id.equals(userId);
    }

    public boolean containsUsername(String username) {
        return this.username.equals(username);
    }
}
