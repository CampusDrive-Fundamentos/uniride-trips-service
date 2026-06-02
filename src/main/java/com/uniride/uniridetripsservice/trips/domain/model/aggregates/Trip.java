package com.uniride.uniridetripsservice.trips.domain.model.aggregates;

import com.uniride.uniridetripsservice.shared.domain.model.entities.AuditableModel;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor
public class Trip extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    public Trip(Long bookingId, Long driverId) {
        this.bookingId = bookingId;
        this.driverId = driverId;
        this.status = TripStatus.PENDING;
    }

    public void startTrip() {
        if (this.status != TripStatus.PENDING) {
            throw new IllegalStateException("El viaje solo puede iniciar si está en estado PENDING.");
        }
        this.status = TripStatus.IN_PROGRESS;
    }

    public void completeTrip() {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException("El viaje debe estar IN_PROGRESS para poder completarse.");
        }
        this.status = TripStatus.COMPLETED;
    }
}