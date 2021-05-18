package h04;

import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

public class Utils {

    public static final long SEED = new Random().nextLong();
    public static final Random RANDOM = new Random(SEED);

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestAbortedException("Class " + e.getMessage() + " not found", e);
        }
    }

    @Test
    public void printSeed() {
        System.out.println("Seed: " + SEED);
    }

    private static final Map<Class<?>, Boolean> CLASS_CORRECT_LOOKUP = new HashMap<>();

    public static boolean definitionCorrect(Class<?> c) {
        if (CLASS_CORRECT_LOOKUP.containsKey(c))
            return CLASS_CORRECT_LOOKUP.get(c);

        try {
            c.getDeclaredMethod(c.getDeclaredAnnotation(DefinitionCheck.class).value()).invoke(null);
            CLASS_CORRECT_LOOKUP.put(c, true);
        } catch (Exception e) {
            CLASS_CORRECT_LOOKUP.put(c, false);
        }

        return CLASS_CORRECT_LOOKUP.get(c);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DefinitionCheck {
        String value();
    }
}
