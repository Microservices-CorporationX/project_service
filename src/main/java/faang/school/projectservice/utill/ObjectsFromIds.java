package faang.school.projectservice.utill;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectsFromIds {

    public static  <T> List<T> getObjects(List<T> objects,
                                   List<Long> idsFromDto,
                                   Function<T, Long> objectToId,
                                   Function<List<Long>, List<T>> idsToObjects) {
        if (objects == null) {
            if (idsFromDto != null) {
                return idsToObjects.apply(idsFromDto);
            }
        } else {
            Set<Long> idsFromDatabase = objects.stream()
                    .map(objectToId)
                    .collect(Collectors.toSet());
            idsFromDto.removeAll(idsFromDatabase);
            List<T> objectsFromDto = idsToObjects.apply(idsFromDto);
            objects.addAll(objectsFromDto);
        }
        return objects;
    }

    public static  <T> T getObject(T object,
                            Long idFromDto,
                            Function<T, Long> objectToId,
                            Function<Long, T> idToObject) {
        if (object == null) {
            if (idFromDto != null) {
                return idToObject.apply(idFromDto);
            }
        } else if (shouldOverrideField(objectToId.apply(object), idFromDto)) {
            if (idFromDto != null) {
                return idToObject.apply(idFromDto);
            } else {
                return null;
            }
        }
        return object;
    }

    private static boolean shouldOverrideField(Long idInDatabase,
                                        Long idInDto) {
        if (idInDatabase == null) {
            return idInDto != null;
        } else {
            return !idInDatabase.equals(idInDto);
        }
    }
}
