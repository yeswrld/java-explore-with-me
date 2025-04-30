package ru.practicum.service.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.service.location.dto.LocationDto;
import ru.practicum.service.location.model.Location;

@Component
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }

    public static Location toLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }
}
