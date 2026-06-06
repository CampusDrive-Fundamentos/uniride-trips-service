package com.uniride.uniridetripsservice.trips.domain.model.aggregates;

import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    @Test
    void shouldInitializeWithRequestedStatus() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L, 11L));

        assertEquals(TripStatus.REQUESTED, trip.getStatus());
        assertEquals(1L, trip.getBookingId());
        assertEquals(2L, trip.getRouteId());
        assertEquals("San Isidro", trip.getCampus());
        assertEquals("OTP123", trip.getSecurityCode());
        assertEquals(15.0, trip.getTotalAmount());
        assertEquals("CARD", trip.getPaymentMethod());
        assertEquals(2, trip.getPassengers().size());
        assertNull(trip.getDriverId());
    }

    @Test
    void shouldAcceptTripWhenStatusIsRequested() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));

        trip.acceptTrip(100L);

        assertEquals(TripStatus.ACCEPTED, trip.getStatus());
        assertEquals(100L, trip.getDriverId());
    }

    @Test
    void shouldThrowExceptionWhenAcceptingNonRequestedTrip() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);

        assertThrows(IllegalStateException.class, () -> trip.acceptTrip(200L));
    }

    @Test
    void shouldStartTripWhenSecurityCodeMatches() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);

        trip.startTrip("OTP123");

        assertEquals(TripStatus.ACTIVE, trip.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenStartingWithIncorrectCode() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);

        assertThrows(IllegalArgumentException.class, () -> trip.startTrip("WRONG"));
    }

    @Test
    void shouldThrowExceptionWhenStartingNonAcceptedTrip() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));

        assertThrows(IllegalStateException.class, () -> trip.startTrip("OTP123"));
    }

    @Test
    void shouldConfirmPassengerArrivalAndCompleteTrip() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L, 11L));
        trip.acceptTrip(100L);
        trip.startTrip("OTP123");

        trip.confirmPassengerArrival(10L);
        trip.markPassengerAsNoShow(11L);

        trip.completeTrip();

        assertEquals(TripStatus.COMPLETED, trip.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCompletingTripWithPendingPassengers() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L, 11L));
        trip.acceptTrip(100L);
        trip.startTrip("OTP123");

        trip.confirmPassengerArrival(10L);

        assertThrows(IllegalStateException.class, trip::completeTrip);
    }

    @Test
    void shouldCancelTripWhenNotCompleted() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));

        trip.cancelTrip("Driver cancelled");

        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals("Driver cancelled", trip.getCancelReason());
    }

    @Test
    void shouldThrowExceptionWhenCancelingCompletedTrip() {
        Trip trip = new Trip(1L, 2L, "San Isidro", "OTP123", 15.0, "CARD", List.of(10L));
        trip.acceptTrip(100L);
        trip.startTrip("OTP123");
        trip.confirmPassengerArrival(10L);
        trip.completeTrip();

        assertThrows(IllegalStateException.class, () -> trip.cancelTrip("some reason"));
    }
}
