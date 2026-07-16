package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Route;
import com.badrulamin.University_Management.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public Page<Route> findAll(Pageable pageable) {
        return routeRepository.findAll(pageable);
    }

    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
    }

    public Route save(Route route) {
        return routeRepository.save(route);
    }

    public Route update(Long id, Route route) {
        findById(id);
        route.setId(id);
        return routeRepository.save(route);
    }

    public void delete(Long id) {
        findById(id);
        routeRepository.deleteById(id);
    }
}
