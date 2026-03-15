package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Show;

import java.util.Optional;

public interface ShowRepositoryPort {

    Optional<Show> findById(Long showId);
}
