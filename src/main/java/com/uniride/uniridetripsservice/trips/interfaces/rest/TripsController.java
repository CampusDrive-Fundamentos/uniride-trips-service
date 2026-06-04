package com.uniride.uniridetripsservice.trips.interfaces.rest;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.*;
import com.uniride.uniridetripsservice.trips.domain.model.queries.*;
import com.uniride.uniridetripsservice.trips.domain.services.TripCommandService;
import com.uniride.uniridetripsservice.trips.domain.services.TripQueryService;
import com.uniride.uniridetripsservice.trips.interfaces.rest.resources.*;
import com.uniride.uniridetripsservice.trips.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trips")
@Tag(name = "Trips", description = "Endpoints para la gestión del ciclo de vida del viaje (Oficiales)")
public class TripsController {

    private final TripCommandService tripCommandService;
    private final TripQueryService tripQueryService;

    public TripsController(TripCommandService tripCommandService, TripQueryService tripQueryService) {
        this.tripCommandService = tripCommandService;
        this.tripQueryService = tripQueryService;
    }


    @PostMapping
    @Operation(summary = "Crear Viaje", description = "Consumido internamente por Booking para crear el viaje.")
    public ResponseEntity<TripResource> createTrip(@RequestBody @Valid CreateTripRequest request) {
        var command = CreateTripCommandFromResourceAssembler.toCommandFromResource(request);
        Trip trip = tripCommandService.handle(command);
        return new ResponseEntity<>(TripResourceFromEntityAssembler.toResourceFromEntity(trip), HttpStatus.CREATED);
    }

    @GetMapping("/available")
    @Operation(summary = "Obtener Bolsa de Viajes", description = "Devuelve viajes en estado REQUESTED filtrados por campus.")
    public ResponseEntity<List<TripResource>> getAvailableTrips(@RequestParam String campus) {
        var query = new GetAvailableTripsQuery(campus);
        var trips = tripQueryService.handle(query);
        var tripResources = trips.stream()
                .map(TripResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tripResources);
    }

    @PatchMapping("/{tripId}/accept")
    @Operation(summary = "Aceptar Viaje", description = "El taxista toma el viaje (cambia a ACCEPTED).")
    public ResponseEntity<TripResource> acceptTrip(@PathVariable Long tripId) {
        Long driverId = getCurrentUserIdFromToken();
        Trip trip = tripCommandService.handle(new AcceptTripCommand(tripId, driverId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @PostMapping("/{tripId}/start")
    @Operation(summary = "Iniciar Viaje (Seguro)", description = "Pasa a estado ACTIVE usando el OTP de los alumnos.")
    public ResponseEntity<TripResource> startTrip(@PathVariable Long tripId, @RequestBody StartTripRequest request) {
        Trip trip = tripCommandService.handle(new StartTripCommand(tripId, request.securityCode()));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @PatchMapping("/{tripId}/arrivals/{passengerId}")
    @Operation(summary = "Confirmar Llegada", description = "El estudiante marca que llegó a su casa a salvo.")
    public ResponseEntity<TripResource> confirmArrival(@PathVariable Long tripId, @PathVariable Long passengerId) {
        Trip trip = tripCommandService.handle(new ConfirmArrivalCommand(tripId, passengerId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @PostMapping("/{tripId}/passengers/{passengerId}/no-show")
    @Operation(summary = "Marcar No-Show", description = "Penaliza al alumno que no salió a tomar el taxi.")
    public ResponseEntity<TripResource> markNoShow(@PathVariable Long tripId, @PathVariable Long passengerId) {
        Trip trip = tripCommandService.handle(new NoShowPassengerCommand(tripId, passengerId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }


    @PostMapping("/{tripId}/complete")
    @Operation(summary = "Finalizar Viaje", description = "Termina el viaje. Notificará a Finance internamente.")
    public ResponseEntity<TripResource> completeTrip(@PathVariable Long tripId) {
        Trip trip = tripCommandService.handle(new CompleteTripCommand(tripId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @PostMapping("/{tripId}/cancel")
    @Operation(summary = "Cancelar Viaje", description = "Aborta el viaje por fuerza mayor.")
    public ResponseEntity<TripResource> cancelTrip(@PathVariable Long tripId, @RequestBody CancelTripRequest request) {
        Trip trip = tripCommandService.handle(new CancelTripCommand(tripId, request.reason()));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }


    @GetMapping("/{tripId}")
    @Operation(summary = "Detalle del Viaje", description = "Busca un viaje por su ID.")
    public ResponseEntity<TripResource> getTripById(@PathVariable Long tripId) {
        return tripQueryService.handle(new GetTripByIdQuery(tripId))
                .map(trip -> ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/current")
    @Operation(summary = "Mi Viaje Actual", description = "Devuelve el viaje en curso del usuario logueado en el token.")
    public ResponseEntity<TripResource> getCurrentTrip() {
        Long userId = getCurrentUserIdFromToken();
        return tripQueryService.handle(new GetCurrentTripQuery(userId))
                .map(trip -> ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip)))
                .orElse(ResponseEntity.notFound().build());
    }


    private Long getCurrentUserIdFromToken() {
        String usernameOrId = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(usernameOrId);
    }
}