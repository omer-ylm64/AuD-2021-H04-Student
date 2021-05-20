package h04.function;

import h04.provider.RatioOfInversionsProvider;
import h04.provider.RatioOfRunsProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static h04.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkInterface")
class ListToIntFunctionTest {

    public static Class<?> listToIntFunctionClass;
    public static Method apply;

    @BeforeAll
    public static void checkInterface() {
        listToIntFunctionClass = getClassForName("h04.function.ListToIntFunction");

        // is public
        assertTrue(isPublic(listToIntFunctionClass.getModifiers()));

        // is interface
        assertTrue(listToIntFunctionClass.isInterface(), "ListToIntFunction must be an interface");

        // is generic
        assertEquals(1, listToIntFunctionClass.getTypeParameters().length, "ListToIntFunction must be generic");
        assertEquals("T", listToIntFunctionClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // methods
        try {
            apply = listToIntFunctionClass.getDeclaredMethod("apply", List.class);

            assertTrue(isPublic(apply.getModifiers()), "Method apply(List) must be public (implicit or explicit)");
            assertFalse(isStatic(apply.getModifiers()), "Method apply(List) must not be static");
            assertFalse(apply.isDefault(), "Method apply(List) must not be default");
            assertEquals(int.class, apply.getReturnType(), "Method apply(List) does not have correct return type");
            assertEquals("java.util.List<T>", apply.getGenericParameterTypes()[0].getTypeName(),
                    "First parameter of method apply(List) is not correctly parameterized");
            assertEquals(1, apply.getExceptionTypes().length, "Method apply(List) must throw exactly one exception");
            assertEquals(NullPointerException.class, apply.getExceptionTypes()[0], "Method apply(List) must throw NullPointerException");
        } catch (NoSuchMethodException e) {
            fail("Interface ListToIntFunction is missing a required method", e);
        }
    }

    @Test
    public void interfaceDefinitionCorrect() {}
}

@DefinitionCheck("checkClass")
class ConstantListToIntFunctionTest {

    public static Class<?> constantListToIntFunctionClass;
    public static Constructor<?> constructor;
    public static Field intField;
    public static Method apply;

    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(ListToIntFunctionTest.class),
                "ConstantListToIntFunctionTest requires ListToIntFunction to be implemented correctly");

        constantListToIntFunctionClass = getClassForName("h04.function.ConstantListToIntFunction");

        // is public
        assertTrue(isPublic(constantListToIntFunctionClass.getModifiers()));

        // is generic
        assertEquals(1, constantListToIntFunctionClass.getTypeParameters().length, "ConstantListToIntFunction must be generic");
        assertEquals("T", constantListToIntFunctionClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // implements ListToIntFunction<T>
        assertTrue(Arrays.stream(constantListToIntFunctionClass.getGenericInterfaces())
                         .anyMatch(type -> type.getTypeName().equals("h04.function.ListToIntFunction<T>")),
                "ConstantListToIntFunction must implement ListToIntFunction<T>");

        // is not abstract
        assertFalse(isAbstract(constantListToIntFunctionClass.getModifiers()), "ConstantListToIntFunction must not be abstract");

        // constructors
        try {
            constructor = constantListToIntFunctionClass.getDeclaredConstructor(int.class);
        } catch (NoSuchMethodException e) {
            fail("ConstantListToIntFunction is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of ConstantListToIntFunction must be public");

        // fields
        for (Field field : constantListToIntFunctionClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) && isFinal(field.getModifiers()) && field.getType().equals(int.class))
                intField = field;

        assertNotNull(intField, "ConstantListToIntFunction has no field matching the criteria specified by the assignment");

        intField.setAccessible(true);

        // methods
        apply = constantListToIntFunctionClass.getDeclaredMethod("apply", List.class);


        instance = constructor.newInstance(42);
    }

    @Test
    public void testIntArray() throws ReflectiveOperationException {
        assertEquals(42, intField.get(instance));
    }

    @Nested
    class ApplyTests {

        @Test
        public void testIllegalArguments() {
            assertThrows(NullPointerException.class, () -> getActualException(apply, instance, (Object) null),
                    "apply(List) must throw a NullPointerException if invoked with null");
        }

        @Test
        public void testValidArguments() throws ReflectiveOperationException {
            assertEquals(intField.get(instance), apply.invoke(instance, List.of()));
            assertEquals(intField.get(instance), apply.invoke(instance, List.of(1, 2, 3, 4, 5)));
        }
    }
}

@DefinitionCheck("checkClass")
class FunctionOnDegreeOfDisorderTest {

    public static Class<?> functionOnDegreeOfDisorderClass;
    public static Field comparator;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(ListToIntFunctionTest.class),
                "FunctionOnDegreeOfDisorderTest requires ListToIntFunction to be implemented correctly");

        functionOnDegreeOfDisorderClass = getClassForName("h04.function.FunctionOnDegreeOfDisorder");

        // is public
        assertTrue(isPublic(functionOnDegreeOfDisorderClass.getModifiers()));

        // is generic
        assertEquals(1, functionOnDegreeOfDisorderClass.getTypeParameters().length, "FunctionOnDegreeOfDisorder must be generic");
        assertEquals("T", functionOnDegreeOfDisorderClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // implements ListToIntFunction<T>
        assertTrue(Arrays.stream(functionOnDegreeOfDisorderClass.getGenericInterfaces())
                        .anyMatch(type -> type.getTypeName().equals("h04.function.ListToIntFunction<T>")),
                "FunctionOnDegreeOfDisorder must implement ListToIntFunction<T>");

        // is abstract
        assertTrue(isAbstract(functionOnDegreeOfDisorderClass.getModifiers()), "FunctionOnDegreeOfDisorder must be abstract");

        // constructors
        Constructor<?> constructor = null;

        try {
            constructor = functionOnDegreeOfDisorderClass.getDeclaredConstructor(Comparator.class);
        } catch (NoSuchMethodException e) {
            fail("FunctionOnDegreeOfDisorder is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of FunctionOnDegreeOfDisorder must be public");
        assertEquals("java.util.Comparator<? super T>", constructor.getGenericParameterTypes()[0].getTypeName(),
                "First parameter of constructor of FunctionOnDegreeOfDisorder is not parameterized correctly");

        // fields
        for (Field field : functionOnDegreeOfDisorderClass.getDeclaredFields())
            if (isProtected(field.getModifiers()) &&
                    isFinal(field.getModifiers()) &&
                    field.getGenericType().getTypeName().equals("java.util.Comparator<? super T>"))
                comparator = field;

        assertNotNull(comparator, "FunctionOnDegreeOfDisorder has no field matching the criteria specified by the assignment");

        comparator.setAccessible(true);
    }

    @Test
    public void classDefinitionCorrect() {}
}

@DefinitionCheck("checkClass")
class FunctionOnRatioOfRunsTest {

    public static Class<?> functionOnRatioOfRunsClass;
    public static Constructor<?> constructor;
    public static Field doubleToIntFunction;
    public static Method apply;

    private static Object instance;
    private static Object doubleToIntFunctionProxy;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(FunctionOnDegreeOfDisorderTest.class),
                "FunctionOnRatioOfRunsTest requires FunctionOnDegreeOfDisorder to be implemented correctly");
        assumeTrue(definitionCorrect(DoubleToIntFunctionTest.class),
                "FunctionOnRatioOfRunsTest requires DoubleToIntFunction to be implemented correctly");

        functionOnRatioOfRunsClass = getClassForName("h04.function.FunctionOnRatioOfRuns");

        // is public
        assertTrue(isPublic(functionOnRatioOfRunsClass.getModifiers()));

        // is generic
        assertEquals(1, functionOnRatioOfRunsClass.getTypeParameters().length, "FunctionOnRatioOfRuns must be generic");
        assertEquals("T", functionOnRatioOfRunsClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // extends FunctionOnDegreeOfDisorder<T>
        assertEquals("h04.function.FunctionOnDegreeOfDisorder<T>", functionOnRatioOfRunsClass.getGenericSuperclass().getTypeName(),
                "FunctionOnRatioOfRuns must extend FunctionOnDegreeOfDisorder");

        // is not abstract
        assertFalse(isAbstract(functionOnRatioOfRunsClass.getModifiers()), "FunctionOnRatioOfRuns must not be abstract");

        // constructors
        try {
            constructor = functionOnRatioOfRunsClass.getDeclaredConstructor(DoubleToIntFunctionTest.doubleToIntFunctionClass, Comparator.class);
        } catch (NoSuchMethodException e) {
            fail("FunctionOnRatioOfRuns is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of FunctionOnRatioOfRuns must be public");
        assertEquals("java.util.Comparator<? super T>", constructor.getGenericParameterTypes()[1].getTypeName(),
                "Second parameter of constructor of FunctionOnDegreeOfDisorder is not parameterized correctly");

        // fields
        for (Field field : functionOnRatioOfRunsClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) &&
                    isFinal(field.getModifiers()) &&
                    field.getType().equals(DoubleToIntFunctionTest.doubleToIntFunctionClass))
                doubleToIntFunction = field;

        assertNotNull(doubleToIntFunction, "FunctionOnDegreeOfDisorder has no field matching the criteria specified by the assignment");

        doubleToIntFunction.setAccessible(true);

        // methods
        apply = functionOnRatioOfRunsClass.getDeclaredMethod("apply", List.class);


        doubleToIntFunctionProxy = doubleToIntFunctionProxy();
        instance = constructor.newInstance(doubleToIntFunctionProxy, Comparator.naturalOrder());
    }

    @Test
    public void testDoubleToIntFunctionField() throws ReflectiveOperationException {
        assertSame(doubleToIntFunctionProxy, doubleToIntFunction.get(instance), "Field of type DoubleToIntFunction is not set correctly");
    }

    @Nested
    class ApplyTests {

        @Test
        public void testIllegalArguments() {
            assertThrows(NullPointerException.class, () -> getActualException(apply, instance, (Object) null));
        }

        @ParameterizedTest
        @ArgumentsSource(RatioOfRunsProvider.class)
        public void testValidArguments(List<Integer> sequence, int numberOfRuns) throws ReflectiveOperationException {
            assertEquals((int) ((double) numberOfRuns / sequence.size()), apply.invoke(instance, sequence));
        }
    }
}

@DefinitionCheck("checkClass")
class FunctionOnRatioOfInversionsTest {

    public static Class<?> functionOnRatioOfInversionsClass;
    public static Constructor<?> constructor;
    public static Field doubleToIntFunction;
    public static Method apply;

    private static Object instance;
    private static Object doubleToIntFunctionProxy;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(FunctionOnDegreeOfDisorderTest.class),
                "FunctionOnRatioOfInversionsTest requires FunctionOnDegreeOfDisorder to be implemented correctly");
        assumeTrue(definitionCorrect(DoubleToIntFunctionTest.class),
                "FunctionOnRatioOfInversionsTest requires DoubleToIntFunction to be implemented correctly");

        functionOnRatioOfInversionsClass = getClassForName("h04.function.FunctionOnRatioOfInversions");

        // is public
        assertTrue(isPublic(functionOnRatioOfInversionsClass.getModifiers()));

        // is generic
        assertEquals(1, functionOnRatioOfInversionsClass.getTypeParameters().length, "FunctionOnRatioOfInversions must be generic");
        assertEquals("T", functionOnRatioOfInversionsClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // extends FunctionOnDegreeOfDisorder<T>
        assertEquals("h04.function.FunctionOnDegreeOfDisorder<T>", functionOnRatioOfInversionsClass.getGenericSuperclass().getTypeName(),
                "FunctionOnRatioOfInversions must extend FunctionOnDegreeOfDisorder");

        // is not abstract
        assertFalse(isAbstract(functionOnRatioOfInversionsClass.getModifiers()), "FunctionOnRatioOfInversions must not be abstract");

        // constructors
        try {
            constructor = functionOnRatioOfInversionsClass.getDeclaredConstructor(DoubleToIntFunctionTest.doubleToIntFunctionClass, Comparator.class);
        } catch (NoSuchMethodException e) {
            fail("FunctionOnRatioOfInversions is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of FunctionOnRatioOfInversions must be public");
        assertEquals("java.util.Comparator<? super T>", constructor.getGenericParameterTypes()[1].getTypeName(),
                "Second parameter of constructor of FunctionOnDegreeOfDisorder is not parameterized correctly");

        // fields
        for (Field field : functionOnRatioOfInversionsClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) &&
                    isFinal(field.getModifiers()) &&
                    field.getType().equals(DoubleToIntFunctionTest.doubleToIntFunctionClass))
                doubleToIntFunction = field;

        assertNotNull(doubleToIntFunction, "FunctionOnDegreeOfDisorder has no field matching the criteria specified by the assignment");

        doubleToIntFunction.setAccessible(true);

        // methods
        apply = functionOnRatioOfInversionsClass.getDeclaredMethod("apply", List.class);


        doubleToIntFunctionProxy = doubleToIntFunctionProxy();
        instance = constructor.newInstance(doubleToIntFunctionProxy, Comparator.naturalOrder());
    }

    @Test
    public void testDoubleToIntFunctionField() throws ReflectiveOperationException {
        assertSame(doubleToIntFunctionProxy, doubleToIntFunction.get(instance), "Field of type DoubleToIntFunction is not set correctly");
    }

    @Nested
    class ApplyTests {

        @Test
        public void testIllegalArguments() {
            assertThrows(NullPointerException.class, () -> getActualException(apply, instance, (Object) null));
        }

        @ParameterizedTest
        @ArgumentsSource(RatioOfInversionsProvider.class)
        public void testValidArguments(List<Integer> sequence, int numberOfInversions, int maxNumberOfInversions) throws ReflectiveOperationException {
            assertEquals((int) ((double) numberOfInversions / maxNumberOfInversions), apply.invoke(instance, sequence));
        }
    }
}
