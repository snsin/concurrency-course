package course.concurrency.m3_shared.lesson313;

import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MutationExample {
    public static void main(String[] args) {
        MergedAnnotations ma = MergedAnnotations.of(List.of());
        AnnotationMetadata simpleAnnotationMetadata = new SimpleAnnotationMetadata("classA",
                1,
                "classEnclosing",
                "superA",
                false,
                new String[0],
                new String[0],
                new MethodMetadata[0],
                ma);
        int threadsCount = 16;
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        @SuppressWarnings("unchecked")
        Set<String>[] annotationTypesLinkArray = new Set[threadsCount];

        for (int i = 0; i < threadsCount; i++) {
            final int indexCopy = i;
            executorService.submit(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Set<String> aTypes = simpleAnnotationMetadata.getAnnotationTypes();
                annotationTypesLinkArray[indexCopy] = aTypes;
            });
        }
        countDownLatch.countDown();
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 1; i < threadsCount; i++) {
            if (annotationTypesLinkArray[i - 1] != annotationTypesLinkArray[i]) {
                System.out.printf("%4d: %12d != %d\n", i,
                        System.identityHashCode(annotationTypesLinkArray[i - 1]),
                        System.identityHashCode(annotationTypesLinkArray[i]));
            }
        }
    }
}
