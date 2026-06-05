package com.uniride.uniridetripsservice.trips.application.internal.commandservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.*;
import com.uniride.uniridetripsservice.trips.domain.services.TripCommandService;
import com.uniride.uniridetripsservice.trips.infrastructure.outboundservices.finance.FinanceServiceIntegration;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripCommandServiceImpl implements TripCommandService {

    private final TripRepository tripRepository;
    private final FinanceServiceIntegration financeServiceIntegration;

    public TripCommandServiceImpl(TripRepository tripRepository, FinanceServiceIntegration financeServiceIntegration) {
        this.tripRepository = tripRepository;
        this.financeServiceIntegration = financeServiceIntegration;
    }

    @Override
    @Transactional
    public Trip handle(CreateTripCommand command) {
        Trip trip = new Trip(command.bookingId(), command.routeId(), command.campus(), command.securityCode(), command.totalAmount(), command.paymentMethod(), command.passengerIds());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(AcceptTripCommand command) {
        if (financeServiceIntegration.isDriverBlocked(command.driverId())) {
            throw new IllegalStateException("Taxista bloqueado por deuda. No puede aceptar viajes.");
        }

        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        trip.acceptTrip(command.driverId());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(StartTripCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        trip.startTrip(command.securityCode());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(ConfirmArrivalCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        trip.confirmPassengerArrival(command.passengerId());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(NoShowPassengerCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        trip.markPassengerAsNoShow(command.passengerId());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(CompleteTripCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        trip.completeTrip();
        Trip savedTrip = tripRepository.save(trip);

        // ¡ADIÓS AL "CASH" HARDCODEADO! Ahora lee el método real guardado en BD
        financeServiceIntegration.reportTripCompletion(
                savedTrip.getId(),
                savedTrip.getDriverId(),
                savedTrip.getTotalAmount(),
                savedTrip.getPaymentMethod()
        );

        return savedTrip;
    }

    @Override
    @Transactional
    public Trip handle(CancelTripCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        trip.cancelTrip(command.reason());
        return tripRepository.save(trip);
    }
}