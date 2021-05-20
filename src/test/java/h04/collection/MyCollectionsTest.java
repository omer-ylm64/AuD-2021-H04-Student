package h04.collection;

import h04.function.ListToIntFunctionTest;
import h04.provider.RandomListProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static h04.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
class MyCollectionsTest {

    public static Class<?> myCollectionsClass;
    public static Constructor<?> constructor;
    public static Field function, comparator;
    public static Method sort;

    private static Object instance;
    private static Object listToIntFunctionProxy, comparatorInstance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(ListToIntFunctionTest.class),
                "MyCollectionsTest requires ListToIntFunction to be implemented correctly");

        myCollectionsClass = getClassForName("h04.collection.MyCollections");

        // is public
        assertTrue(isPublic(myCollectionsClass.getModifiers()));

        // is generic
        assertEquals(1, myCollectionsClass.getTypeParameters().length, "MyCollections must be generic");
        assertEquals("T", myCollectionsClass.getTypeParameters()[0].getName(), "Type parameter is not named 'T'");

        // is not abstract
        assertFalse(isAbstract(myCollectionsClass.getModifiers()), "MyCollections must not be abstract");

        // constructors
        try {
            constructor = myCollectionsClass.getDeclaredConstructor(ListToIntFunctionTest.listToIntFunctionClass, Comparator.class);
        } catch (NoSuchMethodException e) {
            fail("MyCollections is missing a required constructor", e);
        }

        assertTrue(isPublic(constructor.getModifiers()), "Constructor of MyCollections must be public");
        assertEquals("h04.function.ListToIntFunction<T>", constructor.getGenericParameterTypes()[0].getTypeName(),
                "First parameter of constructor has incorrect type");
        assertEquals("java.util.Comparator<? super T>", constructor.getGenericParameterTypes()[1].getTypeName(),
                "Second parameter of constructor has incorrect type");

        // fields
        for (Field field : myCollectionsClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) &&
                    isFinal(field.getModifiers()) &&
                    field.getGenericType().getTypeName().equals("h04.function.ListToIntFunction<T>"))
                function = field;
            else if (isPrivate(field.getModifiers()) &&
                    isFinal(field.getModifiers()) &&
                    field.getGenericType().getTypeName().equals("java.util.Comparator<? super T>"))
                comparator = field;

        assertNotNull(function, "MyCollections is missing a required field");
        assertNotNull(comparator, "MyCollections is missing a required field");

        function.setAccessible(true);
        comparator.setAccessible(true);

        // methods
        try {
            sort = myCollectionsClass.getDeclaredMethod("sort", List.class);

            assertTrue(isPublic(sort.getModifiers()));
            assertEquals(void.class, sort.getReturnType());
            assertEquals("java.util.List<T>", sort.getGenericParameterTypes()[0].getTypeName());
        } catch (NoSuchMethodException e) {
            fail("MyCollections is missing a required method", e);
        }


        listToIntFunctionProxy = listToIntFunctionProxy();
        comparatorInstance = Comparator.naturalOrder();
        instance = constructor.newInstance(listToIntFunctionProxy, Comparator.naturalOrder());
    }

    @Test
    public void testFields() throws ReflectiveOperationException {
        assertSame(listToIntFunctionProxy, function.get(instance), "Field of type ListToIntFunction<T> is not set correctly");
        assertSame(comparatorInstance, comparator.get(instance), "Field of type Comparator<? super T> is not set correctly");
    }

    @ParameterizedTest
    @ArgumentsSource(RandomListProvider.class)
    public void testValidArguments(List<Integer> sequence) throws ReflectiveOperationException {
        List<Integer> sequenceCopy = new ArrayList<>(sequence);

        sequenceCopy.sort(Comparator.naturalOrder());

        Iterator<?> correctSequence = sequenceCopy.iterator(), actualSequence = ((List<?>) sort.invoke(instance, sequence)).iterator();
        int i = 0;

        while (correctSequence.hasNext())
            assertEquals(correctSequence.next(), actualSequence.next(), "Sequences differ at index " + i++);
    }
}
