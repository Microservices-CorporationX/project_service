package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    static List<Project> getSomeActiveProjects() {
        List<Project> activeProjects = new ArrayList<>();

        Project project1 = Project.builder().id(12L).name("Project 1").status(ProjectStatus.IN_PROGRESS).build();
        Project project2 = Project.builder().id(13L).name("Project 2").status(ProjectStatus.IN_PROGRESS).build();
        Project project3 = Project.builder().id(14L).name("Project 3").status(ProjectStatus.IN_PROGRESS).build();

        activeProjects.add(project1);
        activeProjects.add(project2);
        activeProjects.add(project3);

        return activeProjects;
    }
    static List<Project> getSomeNotActiveProjects() {
        List<Project> activeProjects = new ArrayList<>();

        Project project1 = Project.builder().id(22L).name("Project 11").status(ProjectStatus.CREATED).build();
        Project project2 = Project.builder().id(23L).name("Project 12").status(ProjectStatus.CANCELLED).build();
        Project project3 = Project.builder().id(24L).name("Project 13").status(ProjectStatus.COMPLETED).build();

        activeProjects.add(project1);
        activeProjects.add(project2);
        activeProjects.add(project3);

        return activeProjects;
    }

}
