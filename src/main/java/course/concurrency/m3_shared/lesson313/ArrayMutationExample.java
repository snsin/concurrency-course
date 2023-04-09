package course.concurrency.m3_shared.lesson313;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ArrayMutationExample {

    public static void main(String[] args) {
        String[] interfaceNames = {"InterfaceC", "InterfaceD", "InterfaceE"};
        String[] memberClassNames = {"ClassAbc", "ClassCde"};
        MethodMetadata methodExample = new StandardMethodMetadata(getMethodByName("methodExample"), true);
        MethodMetadata[] annotatedMethods = new MethodMetadata[]{methodExample, methodExample};
        List<MergedAnnotation<?>> collect = methodExample.getAnnotations().stream().collect(Collectors.toList());

        SimpleAnnotationMetadata simpleAnnotationMetadata = new SimpleAnnotationMetadata("classB",
                1,
                "enclosingClassB",
                "superClassB",
                true,
                interfaceNames,
                memberClassNames,
                annotatedMethods,
                MergedAnnotations.of(collect)
        );

        MethodMetadata methodFromString = new StandardMethodMetadata(getMethodByName("fromString"));

        System.out.println(Arrays.toString(simpleAnnotationMetadata.getInterfaceNames()));
        System.out.println(Arrays.toString(simpleAnnotationMetadata.getMemberClassNames()));
        System.out.println(simpleAnnotationMetadata.getAnnotatedMethods("Async"));
        System.out.println(Arrays.toString(simpleAnnotationMetadata.getAnnotations().stream().toArray()));

        // changing arrays element affects to the instance fields
        interfaceNames[2] = "InterfaceFFFF";
        memberClassNames[1] = "ClassXyz";
        annotatedMethods[0] = methodFromString;
        // adding annotation to list after create simpleAnnotationMetadata has no effect
        collect.addAll(methodFromString.getAnnotations().stream().collect(Collectors.toList()));


        System.out.println(Arrays.toString(simpleAnnotationMetadata.getInterfaceNames()));
        System.out.println(Arrays.toString(simpleAnnotationMetadata.getMemberClassNames()));
        System.out.println(simpleAnnotationMetadata.getAnnotatedMethods("Async"));
        System.out.println(Arrays.toString(simpleAnnotationMetadata.getAnnotations().stream().toArray()));

        // next lines serve to reduce number of warnings
        ArrayMutationExample arrayMutationExample = new ArrayMutationExample();
        CompletableFuture<Integer> test = arrayMutationExample.fromString("test");
        System.out.println(test.getNow(null));
        arrayMutationExample.methodExample("test");
    }

    private static Method getMethodByName(String methodName) {
        try {
            return ArrayMutationExample.class.getMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }
    }

    @Async
    public CompletableFuture<Integer> fromString(String s) {
        System.out.println(s);
        return CompletableFuture.completedFuture(0);
    }

    @Async
    public void methodExample(String s) {
        System.out.println(s);
    }
}
