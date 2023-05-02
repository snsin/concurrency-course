package course.concurrency.exams.refactoring;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class MountTableRefresherService {

    private Others.RouterStore routerStore = new Others.RouterStore();
    private long cacheUpdateTimeout;

    /**
     * All router admin clients cached. So no need to create the client again and
     * again. Router admin address(host:port) is used as key to cache RouterClient
     * objects.
     */
    private Others.LoadingCache<String, Others.RouterClient> routerClientsCache;

    private Others.MountTableManager manager;

    /**
     * Removes expired RouterClient from routerClientsCache.
     */
    private ScheduledExecutorService clientCacheCleanerScheduler;

    public void serviceInit() {
        long routerClientMaxLiveTime = 15L;
        this.cacheUpdateTimeout = 10L;
        routerClientsCache = new Others.LoadingCache<String, Others.RouterClient>();
        routerStore.getCachedRecords().stream().map(Others.RouterState::getAdminAddress)
                .forEach(addr -> routerClientsCache.add(addr, new Others.RouterClient()));

        initClientCacheCleaner(routerClientMaxLiveTime);
    }

    public void serviceStop() {
        clientCacheCleanerScheduler.shutdown();
        // remove and close all admin clients
        routerClientsCache.cleanUp();
    }

    private void initClientCacheCleaner(long routerClientMaxLiveTime) {
        ThreadFactory tf = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread();
                t.setName("MountTableRefresh_ClientsCacheCleaner");
                t.setDaemon(true);
                return t;
            }
        };

        clientCacheCleanerScheduler =
                Executors.newSingleThreadScheduledExecutor(tf);
        /*
         * When cleanUp() method is called, expired RouterClient will be removed and
         * closed.
         */
        clientCacheCleanerScheduler.scheduleWithFixedDelay(
                () -> routerClientsCache.cleanUp(), routerClientMaxLiveTime,
                routerClientMaxLiveTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh mount table cache of this router as well as all other routers.
     */
    public void refresh() {

        List<MountTableRefresherTask> refreshers = routerStore.getCachedRecords().stream()
                .map(Others.RouterState::getAdminAddress)
                .filter(a -> Objects.nonNull(a) && a.length() > 0)
                .map(this::createMountTableRefresher)
                .map(this::processRefresh)
                .collect(Collectors.toList());

        logResult(refreshers);
    }

    private MountTableRefresherTask createMountTableRefresher(String a) {
        if (isLocalAdmin(a)) {
            return getLocalRefresher(a);
        }
        return new MountTableRefresherTask(manager, a);
    }

    private MountTableRefresherTask processRefresh(MountTableRefresherTask tr) {
        try {
            return CompletableFuture
                    .supplyAsync(() -> runAndReturn(tr))
                    .get(cacheUpdateTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log("Mount table cache refresher was interrupted.");
        } catch (ExecutionException e) {
            System.err.printf("Exception when refreshing MountTableRefresh_%s %s\n",
                    tr.getAdminAddress(),
                    e.getMessage());
        } catch (TimeoutException e) {
            log("Not all router admins updated their cache");
        }
        return tr;
    }

    private MountTableRefresherTask runAndReturn(MountTableRefresherTask tr) {
        tr.run();
        return tr;
    }

    protected MountTableRefresherTask getLocalRefresher(String adminAddress) {
        return new MountTableRefresherTask(manager, adminAddress);
    }

    private void removeFromCache(String adminAddress) {
        routerClientsCache.invalidate(adminAddress);
    }


    private boolean isLocalAdmin(String adminAddress) {
        return adminAddress.contains("local");
    }

    private void logResult(List<MountTableRefresherTask> refreshThreads) {
        int successCount = 0;
        int failureCount = 0;
        for (MountTableRefresherTask mountTableRefreshThread : refreshThreads) {
            if (mountTableRefreshThread.isSuccess()) {
                successCount++;
            } else {
                failureCount++;
                // remove RouterClient from cache so that new client is created
                removeFromCache(mountTableRefreshThread.getAdminAddress());
            }
        }
        log(String.format(
                "Mount table entries cache refresh successCount=%d,failureCount=%d",
                successCount, failureCount));
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void setCacheUpdateTimeout(long cacheUpdateTimeout) {
        this.cacheUpdateTimeout = cacheUpdateTimeout;
    }

    public void setRouterClientsCache(Others.LoadingCache cache) {
        this.routerClientsCache = cache;
    }

    public void setRouterStore(Others.RouterStore routerStore) {
        this.routerStore = routerStore;
    }

    public void setManager(Others.MountTableManager manager) {
        this.manager = manager;
    }
}