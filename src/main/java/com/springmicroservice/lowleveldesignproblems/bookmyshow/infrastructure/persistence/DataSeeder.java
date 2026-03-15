package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.*;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    public DataSeeder(MovieRepository movieRepository,
                      TheaterRepository theaterRepository,
                      ScreenRepository screenRepository,
                      SeatRepository seatRepository,
                      ShowRepository showRepository,
                      ShowSeatRepository showSeatRepository) {
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        var movie = createMovie();
        var theater = createTheater();
        var screen = createScreen(theater);
        var seats = createSeats(screen);
        var show = createShow(movie, screen);
        createShowSeats(show, seats);
    }

    private MovieEntity createMovie() {
        var movie = new MovieEntity();
        movie.setTitle("Inception");
        return movieRepository.save(movie);
    }

    private TheaterEntity createTheater() {
        var theater = new TheaterEntity();
        theater.setAddress("123 Cinema Street, Bangalore");
        return theaterRepository.save(theater);
    }

    private ScreenEntity createScreen(TheaterEntity theater) {
        var screen = new ScreenEntity();
        screen.setRows(3);
        screen.setColumns(4);
        screen.setTheater(theater);
        return screenRepository.save(screen);
    }

    private List<SeatEntity> createSeats(ScreenEntity screen) {
        var seats = new ArrayList<SeatEntity>();
        for (int row = 0; row < screen.getRows(); row++) {
            for (int col = 0; col < screen.getColumns(); col++) {
                var seat = new SeatEntity();
                seat.setSeatNumber(String.format("%c%d", 'A' + row, col + 1));
                seat.setRow(row);
                seat.setColumn(col);
                seat.setScreen(screen);
                seats.add(seatRepository.save(seat));
            }
        }
        return seats;
    }

    private ShowEntity createShow(MovieEntity movie, ScreenEntity screen) {
        var show = new ShowEntity();
        show.setMovie(movie);
        show.setScreen(screen);
        return showRepository.save(show);
    }

    private void createShowSeats(ShowEntity show, List<SeatEntity> seats) {
        for (SeatEntity seat : seats) {
            var showSeat = new ShowSeatEntity();
            showSeat.setSeat(seat);
            showSeat.setShow(show);
            showSeat.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(showSeat);
        }
    }
}
