package faang.school.projectservice.helpers;

import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;


import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ProjectSearcherTest {
    @Test
    void testFindAllProjectsWithEveryPossibleStart() {
        Project anubis = Project.builder().name("Anubis").build();
        Project hades = Project.builder().name("Hades").build();
        Project zeus = Project.builder().name("Zeus").build();
        Project serapis = Project.builder().name("Serapis").build();
        Project apollo = Project.builder().name("Apollo").build();
        Project sekhmet = Project.builder().name("Sekhmet").build();
        apollo.setParentProject(serapis);
        serapis.setChildren(List.of(apollo));
        serapis.setParentProject(sekhmet);
        sekhmet.setChildren(List.of(serapis));
        sekhmet.setParentProject(anubis);
        hades.setParentProject(anubis);
        anubis.setChildren(List.of(hades, sekhmet));
        anubis.setParentProject(zeus);
        zeus.setChildren(List.of(anubis));

        List<Project> projects = List.of(anubis, hades, zeus, serapis, apollo, sekhmet);
        for (Project start : projects) {
            List<Project> actual = ProjectSearcher.findAllProjects(start);
            assertArrayEquals(projects.stream().sorted(Comparator.comparing(Project::getName)).toArray(),
                    actual.stream().sorted(Comparator.comparing(Project::getName)).toArray());
        }
    }

    @Test
    void testFindAllProjectsWithSingleProject() {
        Project project = Project.builder().name("Project").build();
        List<Project> actual = ProjectSearcher.findAllProjects(project);
        assertEquals(1, actual.size());
        assertEquals(project, actual.get(0));
    }
}