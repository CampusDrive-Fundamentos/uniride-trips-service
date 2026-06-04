package com.uniride.uniridetripsservice.trips.domain.model.aggregates;

import com.uniride.uniridetripsservice.shared.domain.model.entities.AuditableModel;
import com.uniride.uniridetripsservice.trips.domain.model.entities.TripPassenger;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor
public class Trip extends AbstractAggregateRoot<Trip> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false)
    private String campus;

    @Column(nullable = true)
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @Column(nullable = false)
    private String securityCode;

    private String cancelReason;

    @Column(nullable = false)
    private Double totalAmount;

    @ElementCollection
    private List<TripPassenger> passengers = new ArrayList<>();

    public Trip(Long bookingId, Long routeId, String campus, String securityCode, Double totalAmount, List<Long> passengerIds) {
        this.bookingId = bookingId;
        this.routeId = routeId;
        this.campus = campus;
        this.securityCode = securityCode;
        this.totalAmount = totalAmount != null ? totalAmount : 0.0;
        this.status = TripStatus.REQUESTED;

        this.passengers = new ArrayList<>();
        if (passengerIds != null) {
            passengerIds.forEach(id -> this.passengers.add(new TripPassenger(id)));
        }
    }


    public void acceptTrip(Long driverId) {
        if (this.status != TripStatus.REQUESTED) {
            throw new IllegalStateException("Solo se pueden aceptar viajes en estado REQUESTED.");
        }
        this.driverId = driverId;
        this.status = TripStatus.ACCEPTED;
    }

    public void startTrip(String inputSecurityCode) {
        if (this.status != TripStatus.ACCEPTED) {
            throw new IllegalStateException("El viaje debe estar ACCEPTED para poder iniciar.");
        }
        if (!this.securityCode.equals(inputSecurityCode)) {
            throw new IllegalArgumentException("Código de seguridad OTP incorrecto.");
        }
        this.status = TripStatus.ACTIVE;
    }

    public void confirmPassengerArrival(Long passengerId) {
        if (this.status != TripStatus.ACTIVE) {
            throw new IllegalStateException("El viaje debe estar ACTIVE para registrar llegadas.");
        }
        TripPassenger passenger = this.passengers.stream()
                .filter(p -> p.getPassengerId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pasajero no pertenece a este viaje"));
        passenger.markAsArrived();
    }

    public void markPassengerAsNoShow(Long passengerId) {
        if (this.status != TripStatus.ACTIVE) {
            throw new IllegalStateException("El viaje debe estar ACTIVE.");
        }
        TripPassenger passenger = this.passengers.stream()
                .filter(p -> p.getPassengerId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pasajero no pertenece a este viaje"));
        passenger.markAsNoShow();
    }

    public void completeTrip() {
        if (this.status != TripStatus.ACTIVE) {
            throw new IllegalStateException("El viaje debe estar ACTIVE para poder completarse.");
        }
        boolean allArrived = this.passengers.stream()
                .filter(p -> !p.isNoShow())
                .allMatch(TripPassenger::isHasArrived);

        if (!allArrived) {
            throw new IllegalStateException("No se puede completar el viaje. Faltan pasajeros por llegar.");
        }

        this.status = TripStatus.COMPLETED;
    }

    public void cancelTrip(String reason) {
        if (this.status == TripStatus.COMPLETED) {
            throw new IllegalStateException("Un viaje completado no se puede cancelar.");
        }
        this.status = TripStatus.CANCELLED;
        this.cancelReason = reason;
    }
}