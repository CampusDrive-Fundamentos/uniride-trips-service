package com.uniride.uniridetripsservice.trips.interfaces.rest.transform;

import com.uniride.uniridetripsservice.trips.domain.model.commands.CreateTripCommand;
import com.uniride.uniridetripsservice.trips.interfaces.rest.resources.CreateTripRequest;

public class CreateTripCommandFromResourceAssembler {
    public static CreateTripCommand toCommandFromResource(CreateTripRequest resource) {
        return new CreateTripCommand(
                resource.bookingId(),
                resource.routeId(),
                resource.campus(),
                resource.securityCode(),
                resource.passengerIds()
        );
    }
}