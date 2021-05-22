package h04.function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static h04.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
public class LinearRegressionTest {

    public static Class<?> linearRegressionClass;
    public static Method fitFunction;

    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        assumeTrue(definitionCorrect(DoubleToIntFunctionFitterTest.class),
                "LinearRegressionTest requires DoubleToIntFunctionFitter to be implemented correctly");
        assumeTrue(definitionCorrect(LinearDoubleToIntFunctionTest.class),
                "LinearRegressionTest requires LinearDoubleToIntFunction be implemented correctly");

        linearRegressionClass = getClassForName("h04.function.LinearRegression");

        // is public
        assertTrue(isPublic(linearRegressionClass.getModifiers()));

        // is not generic
        assertEquals(0, linearRegressionClass.getTypeParameters().length, "LinearRegression must not be generic");

        // implements DoubleToIntFunctionFitter
        assertEquals(DoubleToIntFunctionFitterTest.doubleToIntFunctionFitterClass, linearRegressionClass.getInterfaces()[0],
                "LinearRegression must implement DoubleToIntFunctionFitter");

        // is not abstract
        assertFalse(isAbstract(linearRegressionClass.getModifiers()), "LinearRegression must not be abstract");

        // methods
        fitFunction = linearRegressionClass.getDeclaredMethod("fitFunction", Integer[].class);


        instance = linearRegressionClass.getDeclaredConstructor().newInstance();
    }

    @Test
    public void testFitFunction() throws ReflectiveOperationException {
        Object result = fitFunction.invoke(instance, (Object) new Integer[] {1, 2, 3, null, null, null, 7, null, null, 9, 10});

        assertEquals(LinearDoubleToIntFunctionTest.linearDoubleToIntFunctionClass, result.getClass(),
                "Returned object does not have type LinearDoubleToIntFunction");
    }
}
