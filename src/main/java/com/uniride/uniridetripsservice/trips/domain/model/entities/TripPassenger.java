package com.uniride.uniridetripsservice.trips.domain.model.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
public class TripPassenger {

    private Long passengerId;
    private boolean hasArrived;
    private LocalDateTime arrivedAt;
    private boolean noShow;

    public TripPassenger(Long passengerId) {
        this.passengerId = passengerId;
        this.hasArrived = false;
        this.noShow = false;
        this.arrivedAt = null;
    }

    public void markAsArrived() {
        if (this.noShow) throw new IllegalStateException("No puede llegar un pasajero marcado como No-Show.");
        this.hasArrived = true;
        this.arrivedAt = LocalDateTime.now();
    }

    public void markAsNoShow() {
        if (this.hasArrived) throw new IllegalStateException("El pasajero ya llegó, no puede ser No-Show.");
        this.noShow = true;
    }
}