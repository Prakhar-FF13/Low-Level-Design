package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.adapter;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Show;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.mapper.EntityMapper;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ShowRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShowRepositoryAdapter implements ShowRepositoryPort {

    private final ShowRepository showRepository;

    public ShowRepositoryAdapter(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    @Override
    public Optional<Show> findById(Long showId) {
        return showRepository.findById(showId)
                .map(EntityMapper::toDomain);
    }
}
