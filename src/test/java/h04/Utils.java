package h04;

import org.opentest4j.TestAbortedException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class Utils {

    public static final long SEED = new Random().nextLong();
    public static final Random RANDOM = new Random(SEED);

    private static final boolean CHECK_FOR_UPDATES = true;

    static {
        if (CHECK_FOR_UPDATES)
            try (Reader reader = new FileReader(".test_version"); BufferedReader bufferedReader = new BufferedReader(reader)) {
                HttpClient client = HttpClient.newBuilder()
                                              .version(HttpClient.Version.HTTP_2)
                                              .followRedirects(HttpClient.Redirect.NORMAL)
                                              .connectTimeout(Duration.ofSeconds(20))
                                              .build();
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://git.rwth-aachen.de/aud-tests/AuD-2021-H04-Student/-/raw/master/.test_version"))
                                                 .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String localVersionString = bufferedReader.readLine(), remoteVersionString = response.body();

                if (response.statusCode() == 200) {
                    Integer[] remoteVersions = Arrays.stream(remoteVersionString.split("\\.", 2))
                                                     .map(Integer::parseInt)
                                                     .toArray(Integer[]::new),
                              localVersions = Arrays.stream(localVersionString.split("\\.", 2))
                                                    .map(Integer::parseInt)
                                                    .toArray(Integer[]::new);

                    if (remoteVersions[0] > localVersions[0] || remoteVersions[1] > localVersions[1])
                        System.out.println("Update available! Local version: " + localVersionString + " -- Remote version: " + remoteVersionString);
                    else
                        System.out.println("Local tests are up to date");
                } else {
                    System.err.println("Unable to fetch version from repository");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println("Seed: " + SEED);
    }

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestAbortedException("Class " + e.getMessage() + " not found", e);
        }
    }

    private static final Map<Class<?>, Boolean> CLASS_CORRECT_LOOKUP = new HashMap<>();

    public static boolean definitionCorrect(Class<?> c) {
        if (CLASS_CORRECT_LOOKUP.containsKey(c))
            return CLASS_CORRECT_LOOKUP.get(c);

        try {
            Method method = c.getDeclaredMethod(c.getDeclaredAnnotation(DefinitionCheck.class).value());

            method.setAccessible(true);
            method.invoke(null);

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

    /**
     * Tries to invoke the given method with the given parameters and throws the actual Throwable
     * that caused the InvocationTargetException
     * @param method   the method to invoke
     * @param instance the instance to invoke the method on
     * @param params   the parameter to invoke the method with
     * @throws Throwable the actual Throwable (Exception)
     */
    public static void getActualException(Method method, Object instance, Object... params) throws Throwable {
        try {
            method.invoke(instance, params);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
