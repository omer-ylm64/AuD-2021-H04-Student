package h04.collection;

import h04.function.ListToIntFunctionTest;
import h04.provider.ListItemProvider;
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
    public static Method sort, adaptiveMergeSortInPlace, selectionSortInPlace;

    private static Object instance;
    private static Object listToIntFunctionProxy, comparatorInstance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(ListToIntFunctionTest.class),
                "MyCollectionsTest requires ListToIntFunction to be implemented correctly");
        assumeTrue(definitionCorrect(ListItemTest.class),
                "MyCollectionsTest requires ListItem to be implemented correctly");

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
            adaptiveMergeSortInPlace = myCollectionsClass.getDeclaredMethod("adaptiveMergeSortInPlace", ListItemTest.listItemClass, int.class);
            selectionSortInPlace = myCollectionsClass.getDeclaredMethod("selectionSortInPlace", ListItemTest.listItemClass);
        } catch (NoSuchMethodException e) {
            fail("MyCollections is missing a required method (some are implemented later)", e);
        }

        assertTrue(isPublic(sort.getModifiers()), "sort(List) must be public");
        assertEquals(void.class, sort.getReturnType(), "sort(List) must have return type void");
        assertEquals("java.util.List<T>", sort.getGenericParameterTypes()[0].getTypeName(),
                "First parameter of sort(List) is not correctly parameterized");

        assertTrue(isPrivate(adaptiveMergeSortInPlace.getModifiers()), "adaptiveMergeSortInPlace(ListItem, int) must be private");
        assertEquals("h04.collection.ListItem<T>", adaptiveMergeSortInPlace.getGenericReturnType().getTypeName(),
                "adaptiveMergeSortInPlace(ListItem, int) must have return type ListItem");
        assertEquals("h04.collection.ListItem<T>", adaptiveMergeSortInPlace.getGenericParameterTypes()[0].getTypeName(),
                "First parameter of adaptiveMergeSortInPlace(ListItem, int) is not correctly parameterized");

        assertTrue(isPrivate(selectionSortInPlace.getModifiers()), "selectionSortInPlace(ListItem) must be private");
        assertEquals("h04.collection.ListItem<T>", selectionSortInPlace.getGenericReturnType().getTypeName(),
                "selectionSortInPlace(ListItem) must have return type ListItem");
        assertEquals("h04.collection.ListItem<T>", selectionSortInPlace.getGenericParameterTypes()[0].getTypeName(),
                "First parameter of selectionSortInPlace(ListItem) is not correctly parameterized");

        adaptiveMergeSortInPlace.setAccessible(true);
        selectionSortInPlace.setAccessible(true);


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
    public void testSort(List<Integer> sequence) throws ReflectiveOperationException {
        List<Integer> sequenceCopy = new ArrayList<>(sequence);

        sequenceCopy.sort(Comparator.naturalOrder());
        sort.invoke(instance, sequence);

        Iterator<?> expectedSequence = sequenceCopy.iterator(), actualSequence = sequence.iterator();
        int i = 0;

        while (expectedSequence.hasNext())
            assertEquals(expectedSequence.next(), actualSequence.next(), "Sequences differ at index " + i++);
    }

    @ParameterizedTest
    @ArgumentsSource(ListItemProvider.class)
    public void testAdaptiveMergeSortInPlace(List<Integer> list, Object unsortedListItem) throws Throwable {
        int i = (int) Proxy.getInvocationHandler(listToIntFunctionProxy)
                           .invoke(listToIntFunctionProxy, ListToIntFunctionTest.apply, new Object[] {list}),
            j = 0,
            listItemSize = ListItemProvider.sizeOfListItem(unsortedListItem);

        List<Object> listItems = new ArrayList<>(list.size());

        ListItemProvider.listItems(listItems, unsortedListItem);

        List<Integer> returnedSequence = new ArrayList<>(list.size());
        Object sortedListItem = adaptiveMergeSortInPlace.invoke(instance, unsortedListItem, i);

        ListItemProvider.listFromListItems(returnedSequence, sortedListItem);
        list.sort(Comparator.naturalOrder());

        Iterator<Integer> expectedSequence = list.iterator(), actualSequence = returnedSequence.iterator();

        while (expectedSequence.hasNext())
            assertEquals(expectedSequence.next(), actualSequence.next(), "Sequences differ at index " + j++);

        assertEquals(listItemSize, ListItemProvider.sizeOfListItem(sortedListItem), "Size of given ListItem and returned one don't match");
        assertTrue(listItems.stream().allMatch(expectedListItem -> {
            List<Object> actualListItems = new ArrayList<>(returnedSequence.size());

            try {
                ListItemProvider.listItems(actualListItems, sortedListItem);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }

            return actualListItems.stream().anyMatch(actualListItem -> actualListItem == expectedListItem);
        }), "At least one ListItem object has been added. ListItem objects returned by invocation of adaptiveMergeSortInPlace(ListItem, int) " +
                    "are not the same created by the provider");
    }

    @ParameterizedTest
    @ArgumentsSource(ListItemProvider.class)
    public void testSelectionSortInPlace(List<Integer> list, Object unsortedListItem) throws Throwable {
        int j = 0,
            listItemSize = ListItemProvider.sizeOfListItem(unsortedListItem);

        List<Object> listItems = new ArrayList<>(list.size());

        ListItemProvider.listItems(listItems, unsortedListItem);

        List<Integer> returnedSequence = new ArrayList<>(list.size());
        Object sortedListItem = selectionSortInPlace.invoke(instance, unsortedListItem);

        ListItemProvider.listFromListItems(returnedSequence, sortedListItem);
        list.sort(Comparator.naturalOrder());

        Iterator<Integer> expectedSequence = list.iterator(), actualSequence = returnedSequence.iterator();

        while (expectedSequence.hasNext())
            assertEquals(expectedSequence.next(), actualSequence.next(), "Sequences differ at index " + j++);

        assertEquals(listItemSize, ListItemProvider.sizeOfListItem(sortedListItem), "Size of given ListItem and returned one don't match");
        assertTrue(listItems.stream().allMatch(expectedListItem -> {
            List<Object> actualListItems = new ArrayList<>(returnedSequence.size());

            try {
                ListItemProvider.listItems(actualListItems, sortedListItem);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }

            return actualListItems.stream().anyMatch(actualListItem -> actualListItem == expectedListItem);
        }), "At least one ListItem object has been added. ListItem objects returned by invocation of selectionSortInPlace(ListItem) " +
                    "are not the same created by the provider");
    }
}
