package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssueDto {

    private String id;
    private String key;
    private String self;
    private WholeIssueInfoFields fields;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WholeIssueInfoFields {

        private IssueType issueType;
        private String description;
        private String summary;
        private Project project;
        private Watches watches;
        private String lastViewed;
        private Priority priority;
        private List<IssueLink> issueLinks;
        private User assignee;
        private User creator;
        private User reporter;
        private String created;
        private String updated;
        private Status status;
        private Votes votes;

        @JsonProperty("sub-tasks")
        private List<Issue> subtasks;

        private Comment comment;
        private Worklog worklog;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Project {
            private String id;
            private String key;
            private String name;
            private String self;
            private AvatarUrls avatarUrls;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class AvatarUrls {
            @JsonProperty("48x48")
            private String avatar48Pixels;

            @JsonProperty("32x32")
            private String avatar32Pixels;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Watches {
            private String self;
            private int watchCount;
            private boolean isWatching;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class IssueLink {
            private String id;
            private String self;
            private IssueLinkType type;
            private Issue inwardIssue;
            private Issue outwardIssue;

            @Getter
            @Setter
            @NoArgsConstructor
            public static class IssueLinkType {
                private String id;
                private String name;
                private String self;
                private String outwardIssue;
                private String inwardIssue;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Issue {
            private String id;
            private String key;
            private String self;
            private IssueFields issueFields;

            @Getter
            @Setter
            @NoArgsConstructor
            public static class IssueFields {
                private String summary;
                private Status status;
                private Priority priority;
                private IssueType issueType;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Status {
            private String id;
            private String self;
            private String name;
            private String description;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Priority {
            private String id;
            private String self;
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class IssueType {
            private String id;
            private String self;
            private String description;
            private String name;
            private boolean subtask;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class User {
            private String self;
            private String accountId;
            private String emailAddress;
            private AvatarUrls avatarUrls;
            private String displayName;
            private boolean active;
            private String timeZone;
            private String accountType;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Votes {
            private String self;
            private int votes;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Comment {
            private List<CommentInfo> comments;
            private String self;
            private int total;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class CommentInfo {
            private String id;
            private String self;
            private User author;
            private String body;
            private User updateAuthor;
            private String created;
            private String updated;
            private int startAt;
            private int maxResults;
            private int total;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Worklog {
            List<WorklogInfo> worklogs;

            @Getter
            @Setter
            @NoArgsConstructor
            public static class WorklogInfo {
                private String self;
                private User author;
                private User updateAuthor;
                private String comment;
                private String created;
                private String updated;
                private String started;
                private String timeSpent;
                private int startAt;
                private int maxResults;
                private int total;
            }
        }
    }
}