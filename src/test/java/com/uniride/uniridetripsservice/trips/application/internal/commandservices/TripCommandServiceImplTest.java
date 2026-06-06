package com.uniride.uniridetripsservice.trips.application.internal.commandservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.*;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import com.uniride.uniridetripsservice.trips.infrastructure.outboundservices.finance.FinanceServiceIntegration;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripCommandServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FinanceServiceIntegration financeServiceIntegration;

    private TripCommandServiceImpl tripCommandService;

    @BeforeEach
    void setUp() {
        tripCommandService = new TripCommandServiceImpl(tripRepository, financeServiceIntegration);
    }

    @Test
    void shouldCreateTripWithRequestedStatus() {
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip result = tripCommandService.handle(new CreateTripCommand(
                1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L, 11L)
        ));

        assertEquals(1L, result.getBookingId());
        assertEquals(2L, result.getRouteId());
        assertEquals("San Isidro", result.getCampus());
        assertEquals("OTP123", result.getSecurityCode());
        assertEquals(15.0, result.getTotalAmount());
        assertEquals("CARD", result.getPaymentMethod());
        assertEquals(TripStatus.REQUESTED, result.getStatus());
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void shouldAcceptTripWhenDriverNotBlocked() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        when(financeServiceIntegration.isDriverBlocked(100L)).thenReturn(false);
        when(tripRepository.findById(5L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        Trip result = tripCommandService.handle(new AcceptTripCommand(5L, 100L));

        assertEquals(TripStatus.ACCEPTED, result.getStatus());
        assertEquals(100L, result.getDriverId());
        verify(financeServiceIntegration).isDriverBlocked(100L);
        verify(tripRepository).findById(5L);
        verify(tripRepository).save(trip);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingBlockedDriver() {
        when(financeServiceIntegration.isDriverBlocked(100L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                tripCommandService.handle(new AcceptTripCommand(5L, 100L))
        );
        verify(financeServiceIntegration).isDriverBlocked(100L);
    }

    @Test
    void shouldStartTripWhenTripExists() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);

        when(tripRepository.findById(5L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        Trip result = tripCommandService.handle(new StartTripCommand(5L, "OTP123"));

        assertEquals(TripStatus.ACTIVE, result.getStatus());
        verify(tripRepository).findById(5L);
        verify(tripRepository).save(trip);
    }

    @Test
    void shouldCompleteTripAndReportToFinance() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);
        trip.startTrip("OTP123");
        trip.confirmPassengerArrival(10L);

        when(tripRepository.findById(5L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        Trip result = tripCommandService.handle(new CompleteTripCommand(5L));

        assertEquals(TripStatus.COMPLETED, result.getStatus());
        verify(financeServiceIntegration).reportTripCompletion(
                trip.getId(),
                100L,
                15.0,
                "CARD"
        );
        verify(tripRepository).findById(5L);
        verify(tripRepository).save(trip);
    }

    @Test
    void shouldCancelTripWhenTripExists() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));

        when(tripRepository.findById(5L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        Trip result = tripCommandService.handle(new CancelTripCommand(5L, "Cancelled by user"));

        assertEquals(TripStatus.CANCELLED, result.getStatus());
        assertEquals("Cancelled by user", result.getCancelReason());
        verify(tripRepository).findById(5L);
        verify(tripRepository).save(trip);
    }
}
