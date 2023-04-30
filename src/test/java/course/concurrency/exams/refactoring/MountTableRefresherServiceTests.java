package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

public class MountTableRefresherServiceTests {

    private MountTableRefresherService service;

    private Others.RouterStore routerStore;
    private Others.MountTableManager manager;
    @SuppressWarnings("rawtypes")
    private Others.LoadingCache routerClientsCache;

    @BeforeEach
    public void setUpStreams() {
        service = new MountTableRefresherService();
        service.setCacheUpdateTimeout(1000);
        routerStore = mock(Others.RouterStore.class);
        service.setRouterStore(routerStore);
        manager = mock(Others.MountTableManager.class);
        service.setManager(manager);
        routerClientsCache = mock(Others.LoadingCache.class);
        service.setRouterClientsCache(routerClientsCache);
        // service.serviceInit(); // needed for complex class testing, not for now
    }

    @AfterEach
    public void restoreStreams() {
        // service.serviceStop();
    }

    @Test
    @DisplayName("All tasks are completed successfully")
    public void allDone() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService, times(0)).log("Not all router admins updated their cache");
        verify(mockedService).log("Mount table entries cache refresh successCount=4,failureCount=0");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("All tasks failed")
    public void noSuccessfulTasks() {

        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("999", "local3", "765", "local7");

        when(manager.refresh()).thenReturn(false);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService, times(0)).log("Not all router admins updated their cache");
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
        List<String> adminAddresses = states.stream()
                .map(Others.RouterState::getAdminAddress)
                .collect(toList());
        verify(routerClientsCache, times(4)).invalidate(argThat(adminAddresses::contains));
    }

    @Test
    @DisplayName("Some tasks failed")
    public void halfSuccessedTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("333", "localA", "135", "localC");

        when(manager.refresh()).thenReturn(false)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService, times(0)).log("Not all router admins updated their cache");
        verify(mockedService).log("Mount table entries cache refresh successCount=2,failureCount=2");
        List<String> adminAddresses = states.stream()
                .map(Others.RouterState::getAdminAddress)
                .collect(toList());
        verify(routerClientsCache, times(2)).invalidate(argThat(adminAddresses::contains));
    }

    @Test
    @DisplayName("One task completed with exception")
    public void exceptionInOneTask() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("7788", "localR8", "4365", "localL0");

        when(manager.refresh()).thenReturn(true)
                .thenThrow(RuntimeException.class)
                .thenReturn(true)
                .thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService, times(0)).log("Not all router admins updated their cache");
        verify(mockedService).log("Mount table entries cache refresh successCount=3,failureCount=1");
        List<String> adminAddresses = states.stream()
                .map(Others.RouterState::getAdminAddress)
                .collect(toList());
        verify(routerClientsCache).invalidate(argThat(adminAddresses::contains));
    }

    @Test
    @DisplayName("One task exceeds timeout")
    public void oneTaskExceedTimeout() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("addr", "localD7", "9987", "local90");

        when(manager.refresh()).thenReturn(true)
                .thenAnswer(new AnswersWithDelay(1500L, invocation -> true))
                .thenReturn(true)
                .thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Not all router admins updated their cache");
        verify(mockedService).log("Mount table entries cache refresh successCount=3,failureCount=1");
        List<String> adminAddresses = states.stream()
                .map(Others.RouterState::getAdminAddress)
                .collect(toList());
        verify(routerClientsCache).invalidate(argThat(adminAddresses::contains));
    }

}
