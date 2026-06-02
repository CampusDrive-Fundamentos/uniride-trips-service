package com.uniride.uniridetripsservice.trips.interfaces.rest;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CompleteTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.StartTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetActiveTripByDriverIdQuery;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetTripByIdQuery;
import com.uniride.uniridetripsservice.trips.domain.services.TripCommandService;
import com.uniride.uniridetripsservice.trips.domain.services.TripQueryService;
import com.uniride.uniridetripsservice.trips.interfaces.rest.resources.CreateTripRequest;
import com.uniride.uniridetripsservice.trips.interfaces.rest.resources.TripResource;
import com.uniride.uniridetripsservice.trips.interfaces.rest.transform.CreateTripCommandFromResourceAssembler;
import com.uniride.uniridetripsservice.trips.interfaces.rest.transform.TripResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@Tag(name = "Trips", description = "Endpoints para la gestión del ciclo de vida del viaje")
public class TripsController {

    private final TripCommandService tripCommandService;
    private final TripQueryService tripQueryService;

    public TripsController(TripCommandService tripCommandService, TripQueryService tripQueryService) {
        this.tripCommandService = tripCommandService;
        this.tripQueryService = tripQueryService;
    }

    @PostMapping
    @Operation(summary = "Crear Viaje", description = "Crea un nuevo viaje vinculando una reserva y un conductor.")
    public ResponseEntity<TripResource> createTrip(@RequestBody @Valid CreateTripRequest request) {
        var command = CreateTripCommandFromResourceAssembler.toCommandFromResource(request);
        Trip trip = tripCommandService.handle(command);
        return new ResponseEntity<>(TripResourceFromEntityAssembler.toResourceFromEntity(trip), HttpStatus.CREATED);
    }

    @PostMapping("/{tripId}/start")
    @Operation(summary = "Iniciar Viaje", description = "Cambia el estado del viaje a IN_PROGRESS una vez validados los pasajeros.")
    public ResponseEntity<TripResource> startTrip(@PathVariable Long tripId) {
        Trip trip = tripCommandService.handle(new StartTripCommand(tripId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @PostMapping("/{tripId}/complete")
    @Operation(summary = "Completar Viaje", description = "Finaliza el viaje cambiando su estado a COMPLETED.")
    public ResponseEntity<TripResource> completeTrip(@PathVariable Long tripId) {
        Trip trip = tripCommandService.handle(new CompleteTripCommand(tripId));
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip));
    }

    @GetMapping("/{tripId}")
    @Operation(summary = "Obtener Viaje por ID", description = "Recupera los detalles de un viaje específico.")
    public ResponseEntity<TripResource> getTripById(@PathVariable Long tripId) {
        return tripQueryService.handle(new GetTripByIdQuery(tripId))
                .map(trip -> ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}/active")
    @Operation(summary = "Obtener Viaje Activo", description = "Busca el viaje actualmente IN_PROGRESS para un conductor.")
    public ResponseEntity<TripResource> getActiveTripByDriverId(@PathVariable Long driverId) {
        return tripQueryService.handle(new GetActiveTripByDriverIdQuery(driverId))
                .map(trip -> ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip)))
                .orElse(ResponseEntity.notFound().build());
    }
}