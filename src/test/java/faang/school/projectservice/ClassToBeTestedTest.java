package faang.school.projectservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassToBeTestedTest {
    ClassToBeTested classToBeTested = new ClassToBeTested();

    @Test
    void functionToBeTested() {
        Assertions.assertDoesNotThrow(() -> classToBeTested.functionToBeTested(true));
        Assertions.assertDoesNotThrow(() -> classToBeTested.functionToBeTested(false));
    }
}