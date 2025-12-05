// java
package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FireStationRepositoryTest {

    @Mock
    private JSONFileReaderRepository jsonFileReaderRepository;

    private FireStationRepository fireStationRepository;

    private FireStation sample(String address, int station) {
        FireStation fs = new FireStation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_shouldLoadFireStationsFromJson() {
        List<FireStation> stations = List.of(sample("123 St", 1), sample("456 St", 2));

        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(stations);

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        List<FireStation> result = fireStationRepository.findAll();
        assertEquals(2, result.size());
        assertEquals("123 St", result.get(0).getAddress());
    }

    @Test
    void add_shouldAddFireStationAndPersist() {
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>());

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        FireStation newFS = sample("ABC Street", 3);
        fireStationRepository.add(newFS);

        List<FireStation> result = fireStationRepository.findAll();
        assertEquals(1, result.size());
        assertEquals("ABC Street", result.get(0).getAddress());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("firestations"), anyList());
    }

    @Test
    void findByAddress_shouldReturnMatch() {
        List<FireStation> stations = List.of(sample("123 St", 1));
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(stations);

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        Optional<FireStation> found = fireStationRepository.findByAddress("123 St");

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getStation());
    }

    @Test
    void findByAddress_shouldReturnEmptyWhenNotFound() {
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>());

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        assertTrue(fireStationRepository.findByAddress("missing").isEmpty());
    }

    @Test
    void updateFireStation_shouldUpdateStationAndPersist() {
        FireStation fs = sample("123 St", 1);
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>(List.of(fs)));

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        boolean result = fireStationRepository.updateFireStation("123 St", 9);

        assertTrue(result);
        assertEquals(9, fireStationRepository.findAll().get(0).getStation());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("firestations"), anyList());
    }

    @Test
    void updateFireStation_shouldReturnFalseWhenNotFound() {
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>());

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        assertFalse(fireStationRepository.updateFireStation("NA", 5));
        verify(jsonFileReaderRepository, never()).writeList(anyString(), anyList());
    }

    @Test
    void deleteByAddress_shouldRemoveAndPersist() {
        FireStation fs = sample("123 St", 1);
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>(List.of(fs)));

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        boolean deleted = fireStationRepository.deleteByAddress("123 St");

        assertTrue(deleted);
        assertEquals(0, fireStationRepository.findAll().size());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("firestations"), anyList());
    }

    @Test
    void deleteByAddress_shouldReturnFalseWhenMissing() {
        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(new ArrayList<>());

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        assertFalse(fireStationRepository.deleteByAddress("missing"));
        verify(jsonFileReaderRepository, never()).writeList(anyString(), anyList());
    }

    @Test
    void deleteByStationNumber_shouldDeleteAllMatches() {
        List<FireStation> stations = new ArrayList<>();
        stations.add(sample("A", 1));
        stations.add(sample("B", 1));
        stations.add(sample("C", 2));

        when(jsonFileReaderRepository.readList("firestations", FireStation.class))
                .thenReturn(stations);

        fireStationRepository = new FireStationRepository(jsonFileReaderRepository);
        fireStationRepository.init();

        boolean result = fireStationRepository.deleteByStationNumber(1);

        assertTrue(result);
        assertEquals(1, fireStationRepository.findAll().size());
        assertEquals("C", fireStationRepository.findAll().get(0).getAddress());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("firestations"), anyList());
    }
}
