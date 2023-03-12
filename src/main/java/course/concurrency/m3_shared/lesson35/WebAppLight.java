package course.concurrency.m3_shared.lesson35;


import java.util.*;
import java.util.concurrent.*;

public class WebAppLight {

    private final List<String> servePathSpecs = new ArrayList<>();

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 20_000_000; i++) {
            WebAppLight webApp = new WebAppLight();
            Future<?> futureA = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/a"));
            Future<?> futureB = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/b"));
            Future<?> futureC = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/c"));
            Future<?> futureD = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/d"));
            Future<?> futureE = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/e"));
            Future<?> futureF = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/f"));
            Future<?> futureG = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/g"));
            Future<?> futureH = fixedThreadPool.submit(() -> webApp.addServePathSpec("path/h"));
            try {
                futureA.get();
                futureB.get();
                futureC.get();
                futureD.get();
                futureE.get();
                futureF.get();
                futureG.get();
                futureH.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("iteration: " + i);
                throw new RuntimeException(e);
            }
            if (webApp.getServePathSpecs().length != 8) {
                System.out.println("***: " + Arrays.toString(webApp.getServePathSpecs()) + " :***");
            }
            webApp.configureServlets();
        }
        fixedThreadPool.shutdown();
    }

    void addServePathSpec(String path) {
        synchronized (servePathSpecs) {
            this.servePathSpecs.add(path);
        }
    }

    public String[] getServePathSpecs() {
        String[] paths;
        synchronized (servePathSpecs) {
            paths = this.servePathSpecs.toArray(new String[0]);
        }
        return paths;
    }

    public void configureServlets() {
        setup();

        synchronized (servePathSpecs) {
            for (String path : this.servePathSpecs) {
                serve(path).with(Dispatcher.class);
            }
        }

    }

    private Binder serve(String path) {
        return new Binder(path, map);
    }

    public void setup() {
    }


    private static class Dispatcher {
    }

    private static class Binder {
        private final String path;

        private final Map<String, Object> registry;

        public Binder(String path, Map<String, Object> registry) {
            this.registry = Objects.requireNonNull(registry);
            this.path = Objects.requireNonNullElse(path, "empty");
        }

        public <T> void with(Class<T> tClass) {
            registry.put(path, tClass);
        }
    }
}
