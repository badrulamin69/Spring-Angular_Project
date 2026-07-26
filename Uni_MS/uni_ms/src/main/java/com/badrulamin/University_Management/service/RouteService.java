package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Route;
import com.badrulamin.University_Management.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Route> findAll(Pageable pageable) {
        return routeRepository.findAll(pageable);
    }

    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
    }

    @Transactional
    public Route save(Route route) {
        return routeRepository.save(route);
    }

    @Transactional
    public Route update(Long id, Route incoming) {
        Route existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return routeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        routeRepository.deleteById(id);
    }
}