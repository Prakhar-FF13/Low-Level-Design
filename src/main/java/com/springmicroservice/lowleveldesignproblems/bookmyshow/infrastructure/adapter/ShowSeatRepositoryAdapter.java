package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.adapter;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowSeatRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.mapper.EntityMapper;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowSeatEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ShowSeatRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShowSeatRepositoryAdapter implements ShowSeatRepositoryPort {

    private final ShowSeatRepository showSeatRepository;

    public ShowSeatRepositoryAdapter(ShowSeatRepository showSeatRepository) {
        this.showSeatRepository = showSeatRepository;
    }

    @Override
    public List<ShowSeats> findByShowIdAndStatus(Long showId, SeatStatus status) {
        return showSeatRepository.findByShowShowIdAndStatus(showId, status).stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowSeats> findByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return showSeatRepository.findAllById(ids).stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowSeats> findByIdInForUpdate(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return showSeatRepository.findByIdInForUpdate(ids).stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ShowSeats save(ShowSeats showSeats) {
        ShowSeatEntity entity = EntityMapper.toEntity(showSeats);
        ShowSeatEntity saved = showSeatRepository.save(entity);
        return EntityMapper.toDomain(saved);
    }

    @Override
    public List<ShowSeats> saveAll(List<ShowSeats> showSeatsList) {
        if (showSeatsList == null || showSeatsList.isEmpty()) {
            return List.of();
        }
        List<ShowSeatEntity> entities = showSeatsList.stream()
                .map(EntityMapper::toEntity)
                .collect(Collectors.toList());
        return showSeatRepository.saveAll(entities).stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
