package h04.function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static h04.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkInterface")
class DoubleToIntFunctionTest {

    public static Class<?> doubleToIntFunctionClass;
    public static Method apply;

    @BeforeAll
    public static void checkInterface() {
        doubleToIntFunctionClass = getClassForName("h04.function.DoubleToIntFunction");

        // is public
        assertTrue(isPublic(doubleToIntFunctionClass.getModifiers()));

        // is interface
        assertTrue(doubleToIntFunctionClass.isInterface(), "DoubleToIntFunction must be an interface");

        // is not generic
        assertEquals(0, doubleToIntFunctionClass.getTypeParameters().length, "DoubleToIntFunction must not be generic");

        // methods
        try {
            apply = doubleToIntFunctionClass.getDeclaredMethod("apply", double.class);

            assertTrue(isPublic(apply.getModifiers()), "Method apply(double) must be public (implicit or explicit)");
            assertFalse(isStatic(apply.getModifiers()), "Method apply(double) must not be static");
            assertFalse(apply.isDefault(), "Method apply(double) must not be default");
            assertEquals(int.class, apply.getReturnType(), "Method apply(double) does not have correct return type");
            assertEquals(1, apply.getExceptionTypes().length, "Method apply(double) must throw exactly one exception");
            assertEquals(IllegalArgumentException.class, apply.getExceptionTypes()[0], "Method apply(double) must throw IllegalArgumentException");
        } catch (NoSuchMethodException e) {
            fail("Interface DoubleToIntFunction is missing a required method", e);
        }
    }

    @Test
    public void interfaceDefinitionCorrect() {}
}

@SuppressWarnings("RedundantCast")
@DefinitionCheck("checkClass")
class ArrayDoubleToIntFunctionTest {

    public static Class<?> arrayDoubleToIntFunctionClass;
    public static Constructor<?> constructor;
    public static Field ints;
    public static Method apply;

    private static final int[] GIVEN_INTS = new int[] {10, 4, 5, 1, 8};
    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(DoubleToIntFunctionTest.class),
                "ArrayDoubleToIntFunctionTest requires DoubleToIntFunction to be implemented correctly");

        arrayDoubleToIntFunctionClass = getClassForName("h04.function.ArrayDoubleToIntFunction");

        // is public
        assertTrue(isPublic(arrayDoubleToIntFunctionClass.getModifiers()));

        // is not generic
        assertEquals(0, arrayDoubleToIntFunctionClass.getTypeParameters().length, "ArrayDoubleToIntFunction must not be generic");

        // implements DoubleToIntFunction
        assertTrue(Arrays.stream(arrayDoubleToIntFunctionClass.getGenericInterfaces())
                         .anyMatch(type -> type.getTypeName().equals("h04.function.DoubleToIntFunction")),
                "ArrayDoubleToIntFunction must implement DoubleToIntFunction");

        // is not abstract
        assertFalse(isAbstract(arrayDoubleToIntFunctionClass.getModifiers()), "ArrayDoubleToIntFunction must not be abstract");

        // constructors
        try {
            constructor = arrayDoubleToIntFunctionClass.getDeclaredConstructor(int[].class);
        } catch (NoSuchMethodException e) {
            fail("ArrayDoubleToIntFunction is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of ArrayDoubleToIntFunction must be public");

        // fields
        for (Field field : arrayDoubleToIntFunctionClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) && isFinal(field.getModifiers()) && field.getType().equals(int[].class))
                ints = field;

        assertNotNull(ints, "ArrayDoubleToIntFunction has no field matching the criteria specified by the assignment");

        ints.setAccessible(true);

        // methods
        apply = arrayDoubleToIntFunctionClass.getDeclaredMethod("apply", double.class);


        instance = constructor.newInstance((Object) GIVEN_INTS);
    }

    @Test
    public void testIntArray() throws ReflectiveOperationException {
        assertEquals(GIVEN_INTS.length, ((int[]) ints.get(instance)).length,
                "Array in ArrayDoubleToIntFunction does not have same length as given one");
        assertNotSame(GIVEN_INTS, ints.get(instance), "Array objects must not be the same. Values must be copied");
    }

    @Nested
    class ApplyTests {

        @Test
        public void testIllegalArguments() {
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, -0.1), "-0.1 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, 1.1), "1.1 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, -1.0), "-1.0 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, 2.0), "2.0 is out of bounds for apply(double)");
        }

        @Test
        public void testValidArguments() throws ReflectiveOperationException {
            int[] expectedValues = new int[] {10, 8, 5, 4, 5, 5, 3, 2, 2, 5, 8};

            for (int i = 0; i < expectedValues.length; i++)
                assertEquals(expectedValues[i], apply.invoke(instance, i * 0.1));
        }
    }
}

@DefinitionCheck("checkClass")
class LinearDoubleToIntFunctionTest {

    public static Class<?> linearDoubleToIntFunctionClass;
    public static Constructor<?> constructor;
    public static Field a, b;
    public static Method apply;

    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(DoubleToIntFunctionTest.class),
                "LinearDoubleToIntFunctionTest requires DoubleToIntFunction to be implemented correctly");

        linearDoubleToIntFunctionClass = getClassForName("h04.function.LinearDoubleToIntFunction");

        // is public
        assertTrue(isPublic(linearDoubleToIntFunctionClass.getModifiers()));

        // is not generic
        assertEquals(0, linearDoubleToIntFunctionClass.getTypeParameters().length, "LinearDoubleToIntFunction must not be generic");

        // implements DoubleToIntFunction
        assertTrue(Arrays.stream(linearDoubleToIntFunctionClass.getGenericInterfaces())
                        .anyMatch(type -> type.getTypeName().equals("h04.function.DoubleToIntFunction")),
                "LinearDoubleToIntFunction must implement DoubleToIntFunction");

        // is not abstract
        assertFalse(isAbstract(linearDoubleToIntFunctionClass.getModifiers()), "LinearDoubleToIntFunction must not be abstract");

        // constructors
        try {
            constructor = linearDoubleToIntFunctionClass.getDeclaredConstructor(double.class, double.class);
        } catch (NoSuchMethodException e) {
            fail("LinearDoubleToIntFunction is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of LinearDoubleToIntFunction must be public");

        // fields
        try {
            a = linearDoubleToIntFunctionClass.getDeclaredField("a");
            b = linearDoubleToIntFunctionClass.getDeclaredField("b");
        } catch (NoSuchFieldException e) {
            fail("LinearDoubleToIntFunction is missing one or more required fields", e);
        }

        assertFalse(isStatic(a.getModifiers()), "Field a in LinearDoubleToIntFunction must not be static");
        assertTrue(isFinal(a.getModifiers()), "Field a in LinearDoubleToIntFunction must be final");
        assertEquals(double.class, a.getType(), "Field a in LinearDoubleToIntFunction has wrong type");

        assertFalse(isStatic(b.getModifiers()), "Field b in LinearDoubleToIntFunction must not be static");
        assertTrue(isFinal(b.getModifiers()), "Field b in LinearDoubleToIntFunction must be final");
        assertEquals(double.class, b.getType(), "Field b in LinearDoubleToIntFunction has wrong type");

        a.setAccessible(true);
        b.setAccessible(true);

        // methods
        apply = linearDoubleToIntFunctionClass.getDeclaredMethod("apply", double.class);


        instance = constructor.newInstance(4.0, 2.0);
    }

    @Test
    public void testFields() throws ReflectiveOperationException {
        assertEquals(4.0, a.get(instance));
        assertEquals(2.0, b.get(instance));
    }

    @Nested
    class ApplyTests {

        @Test
        public void testIllegalArguments() {
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, -0.1), "-0.1 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, 1.1), "1.1 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, -1.0), "-1.0 is out of bounds for apply(double)");
            assertThrows(IllegalArgumentException.class, () -> getActualException(apply, instance, 2.0), "2.0 is out of bounds for apply(double)");
        }

        @Test
        public void testValidArguments() throws ReflectiveOperationException {
            int[] expectedValues = new int[] {2, 2, 3, 3, 4, 4, 4, 5, 5, 6, 6};

            for (int i = 0; i < expectedValues.length; i++)
                assertEquals(expectedValues[i], apply.invoke(instance, i * 0.1));
        }
    }
}
