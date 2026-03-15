package com.springmicroservice.lowleveldesignproblems.bookmyshow;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.MovieEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ScreenEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.SeatEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowSeatEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TheaterEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.MovieRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ScreenRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.SeatRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ShowRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ShowSeatRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.TheaterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("test")
public class TestDataSeeder {

    @Bean
    public CommandLineRunner seedTestData(MovieRepository movieRepository,
                                         TheaterRepository theaterRepository,
                                         ScreenRepository screenRepository,
                                         SeatRepository seatRepository,
                                         ShowRepository showRepository,
                                         ShowSeatRepository showSeatRepository) {
        return args -> {
            if (movieRepository.count() > 0) {
                return;
            }

            var movie = createMovie(movieRepository);
            var theater = createTheater(theaterRepository);
            var screen = createScreen(theaterRepository, screenRepository, theater);
            var seats = createSeats(seatRepository, screen);
            var show = createShow(movieRepository, showRepository, movie, screen);
            createShowSeats(showSeatRepository, show, seats);
        };
    }

    private MovieEntity createMovie(MovieRepository repo) {
        var movie = new MovieEntity();
        movie.setTitle("Inception");
        return repo.save(movie);
    }

    private TheaterEntity createTheater(TheaterRepository repo) {
        var theater = new TheaterEntity();
        theater.setAddress("123 Cinema Street, Bangalore");
        return repo.save(theater);
    }

    private ScreenEntity createScreen(TheaterRepository theaterRepo, ScreenRepository screenRepo, TheaterEntity theater) {
        var screen = new ScreenEntity();
        screen.setRows(3);
        screen.setColumns(4);
        screen.setTheater(theater);
        return screenRepo.save(screen);
    }

    private List<SeatEntity> createSeats(SeatRepository repo, ScreenEntity screen) {
        var seats = new ArrayList<SeatEntity>();
        for (int row = 0; row < screen.getRows(); row++) {
            for (int col = 0; col < screen.getColumns(); col++) {
                var seat = new SeatEntity();
                seat.setSeatNumber(String.format("%c%d", 'A' + row, col + 1));
                seat.setRow(row);
                seat.setColumn(col);
                seat.setScreen(screen);
                seats.add(repo.save(seat));
            }
        }
        return seats;
    }

    private ShowEntity createShow(MovieRepository movieRepo, ShowRepository showRepo, MovieEntity movie, ScreenEntity screen) {
        var show = new ShowEntity();
        show.setMovie(movie);
        show.setScreen(screen);
        return showRepo.save(show);
    }

    private void createShowSeats(ShowSeatRepository repo, ShowEntity show, List<SeatEntity> seats) {
        for (SeatEntity seat : seats) {
            var showSeat = new ShowSeatEntity();
            showSeat.setSeat(seat);
            showSeat.setShow(show);
            showSeat.setStatus(SeatStatus.AVAILABLE);
            repo.save(showSeat);
        }
    }
}
