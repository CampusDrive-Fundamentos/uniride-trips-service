package com.uniride.uniridetripsservice.trips.application.internal.commandservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CreateTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.StartTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripCommandServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    private TripCommandServiceImpl tripCommandService;

    @BeforeEach
    void setUp() {
        tripCommandService = new TripCommandServiceImpl(tripRepository);
    }

    @Test
    void shouldCreateTripWithPendingStatus() {
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip result = tripCommandService.handle(new CreateTripCommand(1L, 100L));

        assertEquals(1L, result.getBookingId());
        assertEquals(100L, result.getDriverId());
        assertEquals(TripStatus.PENDING, result.getStatus());
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void shouldStartTripWhenTripExists() {
        Trip trip = new Trip(1L, 100L);
        when(tripRepository.findById(5L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        Trip result = tripCommandService.handle(new StartTripCommand(5L));

        assertEquals(TripStatus.IN_PROGRESS, result.getStatus());
        verify(tripRepository).findById(5L);
        verify(tripRepository).save(trip);
    }
}
