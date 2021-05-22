package h04.function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static h04.Utils.*;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPublic;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
public class LinearInterpolationTest {

    public static Class<?> linearInterpolationClass;
    public static Method fitFunction;

    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(DoubleToIntFunctionFitterTest.class),
                "LinearInterpolationTest requires DoubleToIntFunctionFitter to be implemented correctly");
        assumeTrue(definitionCorrect(ArrayDoubleToIntFunctionTest.class),
                "LinearInterpolationTest requires ArrayDoubleToIntFunction be implemented correctly");

        linearInterpolationClass = getClassForName("h04.function.LinearInterpolation");

        // is public
        assertTrue(isPublic(linearInterpolationClass.getModifiers()));

        // is not generic
        assertEquals(0, linearInterpolationClass.getTypeParameters().length, "LinearInterpolation must not be generic");

        // implements DoubleToIntFunctionFitter
        assertEquals(DoubleToIntFunctionFitterTest.doubleToIntFunctionFitterClass, linearInterpolationClass.getInterfaces()[0],
                "LinearInterpolation must implement DoubleToIntFunctionFitter");

        // is not abstract
        assertFalse(isAbstract(linearInterpolationClass.getModifiers()), "LinearInterpolation must not be abstract");

        // methods
        fitFunction = linearInterpolationClass.getDeclaredMethod("fitFunction", Integer[].class);


        instance = linearInterpolationClass.getDeclaredConstructor().newInstance();
    }

    @Test
    public void testFitFunction() throws ReflectiveOperationException {
        Object result = fitFunction.invoke(instance, (Object) new Integer[] {1, 2, 3, null, null, null, 7, null, null, 9, 10});

        assertEquals(ArrayDoubleToIntFunctionTest.arrayDoubleToIntFunctionClass, result.getClass(),
                "Returned object does not have type ArrayDoubleToIntFunction");
    }
}
