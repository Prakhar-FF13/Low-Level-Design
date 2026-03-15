package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;

import java.util.List;

public interface ShowSeatRepositoryPort {

    List<ShowSeats> findByShowIdAndStatus(Long showId, SeatStatus status);

    List<ShowSeats> findByIdIn(List<Long> ids);

    ShowSeats save(ShowSeats showSeats);

    List<ShowSeats> saveAll(List<ShowSeats> showSeats);
}
