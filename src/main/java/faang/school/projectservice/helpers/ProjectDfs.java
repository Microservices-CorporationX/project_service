package faang.school.projectservice.helpers;

import faang.school.projectservice.model.Project;

import java.util.*;

public class ProjectDfs {
    public static List<Project> findAllProjects(Project startVertex) {
        List<Project> result = new ArrayList<>();
        Map<Project, Color> colors = new HashMap<>();
        colors.put(startVertex, Color.WHITE);
        ArrayDeque<Project> stack = new ArrayDeque<>();
        stack.push(startVertex);
        while (!stack.isEmpty()) {
            Project currentVertex = stack.pop();
            if (colors.get(currentVertex) == Color.WHITE) {
                colors.put(currentVertex, Color.GRAY);
                stack.push(currentVertex);
                List<Project> outgoingVertices = new ArrayList<>();
                if (currentVertex.getChildren() != null) {
                    outgoingVertices.addAll(currentVertex.getChildren());
                }
                if (currentVertex.getParentProject() != null) {
                    outgoingVertices.add(currentVertex.getParentProject());
                }
                outgoingVertices.forEach(vertex -> {
                    colors.computeIfAbsent(vertex, key -> Color.WHITE);
                    if (colors.get(vertex) == Color.WHITE) {
                        stack.push(vertex);
                    }
                });
            } else if (colors.get(currentVertex) == Color.GRAY) {
                result.add(currentVertex);
            }
        }
        return result;
    }

    private enum Color {
        WHITE, GRAY
    }
}
