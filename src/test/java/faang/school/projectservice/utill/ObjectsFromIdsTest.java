package faang.school.projectservice.utill;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObjectsFromIdsTest {

    @Test
    void testGetObjects_WithNullObjectsAndNonNullIds() {
        List<Long> idsFromDto = Arrays.asList(1L, 2L, 3L);

        Function<List<Long>, List<String>> idsToObjects = ids ->
                ids.stream().map(String::valueOf).toList();

        List<String> result = ObjectsFromIds.getObjects(null, idsFromDto, null, idsToObjects);

        assertEquals(Arrays.asList("1", "2", "3"), result);
    }

    @Test
    void testGetObjects_WithNonNullObjectsAndEmptyIds() {
        List<String> objects = new ArrayList<>(Arrays.asList("1", "2"));
        List<Long> idsFromDto = new ArrayList<>();

        Function<String, Long> objectToId = Long::valueOf;
        Function<List<Long>, List<String>> idsToObjects = ids ->
                ids.stream().map(String::valueOf).toList();

        List<String> result = ObjectsFromIds.getObjects(objects, idsFromDto, objectToId, idsToObjects);

        assertEquals(Arrays.asList("1", "2"), result);
    }

    @Test
    void testGetObjects_WithPartiallyMatchingIds() {
        List<String> objects = new ArrayList<>(Arrays.asList("1", "2"));
        List<Long> idsFromDto = new ArrayList<>(Arrays.asList(2L, 3L));

        Function<String, Long> objectToId = Long::valueOf;
        Function<List<Long>, List<String>> idsToObjects = ids ->
                ids.stream().map(String::valueOf).toList();

        List<String> result = ObjectsFromIds.getObjects(objects, idsFromDto, objectToId, idsToObjects);

        assertEquals(Arrays.asList("1", "2", "3"), result);
    }

    @Test
    void testGetObject_WithNullObjectAndNonNullId() {
        Long idFromDto = 1L;

        Function<Long, String> idToObject = String::valueOf;

        String result = ObjectsFromIds.getObject(null, idFromDto, null, idToObject);

        assertEquals("1", result);
    }

    @Test
    void testGetObject_WithNonNullObjectAndMatchingId() {
        String object = "1";
        Long idFromDto = 1L;

        Function<String, Long> objectToId = Long::valueOf;
        Function<Long, String> idToObject = String::valueOf;

        String result = ObjectsFromIds.getObject(object, idFromDto, objectToId, idToObject);

        assertEquals("1", result);
    }

    @Test
    void testGetObject_WithNonNullObjectAndNonMatchingId() {
        String object = "1";
        Long idFromDto = 2L;

        Function<String, Long> objectToId = Long::valueOf;
        Function<Long, String> idToObject = String::valueOf;

        String result = ObjectsFromIds.getObject(object, idFromDto, objectToId, idToObject);

        assertEquals("2", result);
    }

    @Test
    void testGetObject_WithNonNullObjectAndNullId() {
        String object = "1";

        Function<String, Long> objectToId = Long::valueOf;
        Function<Long, String> idToObject = String::valueOf;

        String result = ObjectsFromIds.getObject(object, null, objectToId, idToObject);

        assertNull(result);
    }
}