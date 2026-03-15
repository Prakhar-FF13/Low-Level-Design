package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.mapper;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.*;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EntityMapper {

    public static Movie toDomain(MovieEntity entity) {
        if (entity == null) return null;
        Movie movie = new Movie();
        movie.setId(entity.getId());
        movie.setTitle(entity.getTitle());
        if (entity.getShows() != null) {
            movie.setShows(entity.getShows().stream()
                    .map(EntityMapper::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return movie;
    }

    public static MovieEntity toEntity(Movie domain) {
        if (domain == null) return null;
        MovieEntity entity = new MovieEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        if (domain.getShows() != null) {
            entity.setShows(domain.getShows().stream()
                    .map(EntityMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return entity;
    }

    public static Theater toDomain(TheaterEntity entity) {
        if (entity == null) return null;
        Theater theater = new Theater();
        theater.setId(entity.getId());
        theater.setAddress(entity.getAddress());
        if (entity.getScreens() != null) {
            theater.setScreens(entity.getScreens().stream()
                    .map(EntityMapper::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return theater;
    }

    public static TheaterEntity toEntity(Theater domain) {
        if (domain == null) return null;
        TheaterEntity entity = new TheaterEntity();
        entity.setId(domain.getId());
        entity.setAddress(domain.getAddress());
        if (domain.getScreens() != null) {
            entity.setScreens(domain.getScreens().stream()
                    .map(EntityMapper::toEntityScreenShallow)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return entity;
    }

    private static ScreenEntity toEntityScreenShallow(Screen domain) {
        if (domain == null) return null;
        ScreenEntity entity = new ScreenEntity();
        entity.setId(domain.getId());
        entity.setRows(domain.getRows());
        entity.setColumns(domain.getColumns());
        entity.setTheater(toEntityTheaterShallow(domain.getTheater()));
        return entity;
    }

    public static Screen toDomain(ScreenEntity entity) {
        if (entity == null) return null;
        Screen screen = new Screen();
        screen.setId(entity.getId());
        screen.setRows(entity.getRows());
        screen.setColumns(entity.getColumns());
        screen.setTheater(toDomainShallow(entity.getTheater()));
        if (entity.getSeats() != null) {
            screen.setSeats(entity.getSeats().stream()
                    .map(EntityMapper::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return screen;
    }

    private static Theater toDomainShallow(TheaterEntity entity) {
        if (entity == null) return null;
        Theater theater = new Theater();
        theater.setId(entity.getId());
        theater.setAddress(entity.getAddress());
        return theater;
    }

    public static ScreenEntity toEntity(Screen domain) {
        if (domain == null) return null;
        ScreenEntity entity = new ScreenEntity();
        entity.setId(domain.getId());
        entity.setRows(domain.getRows());
        entity.setColumns(domain.getColumns());
        entity.setTheater(toEntityTheaterShallow(domain.getTheater()));
        if (domain.getSeats() != null) {
            entity.setSeats(domain.getSeats().stream()
                    .map(EntityMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return entity;
    }

    private static TheaterEntity toEntityTheaterShallow(Theater domain) {
        if (domain == null) return null;
        TheaterEntity entity = new TheaterEntity();
        entity.setId(domain.getId());
        entity.setAddress(domain.getAddress());
        return entity;
    }

    public static Seat toDomain(SeatEntity entity) {
        if (entity == null) return null;
        Seat seat = new Seat();
        seat.setId(entity.getId());
        seat.setSeatNumber(entity.getSeatNumber());
        seat.setRow(entity.getRow());
        seat.setColumn(entity.getColumn());
        seat.setScreen(toDomainScreenShallow(entity.getScreen()));
        return seat;
    }

    private static Screen toDomainScreenShallow(ScreenEntity entity) {
        if (entity == null) return null;
        Screen screen = new Screen();
        screen.setId(entity.getId());
        screen.setRows(entity.getRows());
        screen.setColumns(entity.getColumns());
        screen.setTheater(toDomainShallow(entity.getTheater()));
        return screen;
    }

    public static SeatEntity toEntity(Seat domain) {
        if (domain == null) return null;
        SeatEntity entity = new SeatEntity();
        entity.setId(domain.getId());
        entity.setSeatNumber(domain.getSeatNumber());
        entity.setRow(domain.getRow());
        entity.setColumn(domain.getColumn());
        entity.setScreen(toEntityScreenShallow(domain.getScreen()));
        return entity;
    }

    public static Show toDomain(ShowEntity entity) {
        if (entity == null) return null;
        Show show = new Show();
        show.setShowId(entity.getShowId());
        show.setMovie(toDomainMovieShallow(entity.getMovie()));
        show.setScreen(toDomainScreenShallow(entity.getScreen()));
        return show;
    }

    private static Movie toDomainMovieShallow(MovieEntity entity) {
        if (entity == null) return null;
        Movie movie = new Movie();
        movie.setId(entity.getId());
        movie.setTitle(entity.getTitle());
        return movie;
    }

    public static ShowEntity toEntity(Show domain) {
        if (domain == null) return null;
        ShowEntity entity = new ShowEntity();
        entity.setShowId(domain.getShowId());
        entity.setMovie(toEntityMovieShallow(domain.getMovie()));
        entity.setScreen(toEntityScreenShallow(domain.getScreen()));
        return entity;
    }

    private static MovieEntity toEntityMovieShallow(Movie domain) {
        if (domain == null) return null;
        MovieEntity entity = new MovieEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        return entity;
    }

    public static ShowSeats toDomain(ShowSeatEntity entity) {
        if (entity == null) return null;
        ShowSeats showSeats = new ShowSeats();
        showSeats.setId(entity.getId());
        showSeats.setSeat(toDomain(entity.getSeat()));
        showSeats.setShow(toDomain(entity.getShow()));
        showSeats.setTicket(toDomainTicketShallow(entity.getTicket()));
        showSeats.setStatus(entity.getStatus());
        return showSeats;
    }

    private static Ticket toDomainTicketShallow(TicketEntity entity) {
        if (entity == null) return null;
        Ticket ticket = new Ticket();
        ticket.setTicketId(entity.getTicketId());
        ticket.setShow(toDomain(entity.getShow()));
        return ticket;
    }

    public static ShowEntity toEntityReference(Show domain) {
        if (domain == null || domain.getShowId() == null) return null;
        ShowEntity entity = new ShowEntity();
        entity.setShowId(domain.getShowId());
        return entity;
    }

    public static SeatEntity toEntityReference(Seat domain) {
        if (domain == null || domain.getId() == null) return null;
        SeatEntity entity = new SeatEntity();
        entity.setId(domain.getId());
        return entity;
    }

    public static TicketEntity toEntityReference(Ticket domain) {
        if (domain == null || domain.getTicketId() == null) return null;
        TicketEntity entity = new TicketEntity();
        entity.setTicketId(domain.getTicketId());
        return entity;
    }

    public static ShowSeatEntity toEntity(ShowSeats domain) {
        if (domain == null) return null;
        ShowSeatEntity entity = new ShowSeatEntity();
        entity.setId(domain.getId());
        entity.setSeat(toEntity(domain.getSeat()));
        entity.setShow(toEntity(domain.getShow()));
        entity.setTicket(domain.getTicket() != null ? toEntityReference(domain.getTicket()) : null);
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public static Ticket toDomain(TicketEntity entity) {
        if (entity == null) return null;
        Ticket ticket = new Ticket();
        ticket.setTicketId(entity.getTicketId());
        ticket.setShow(toDomain(entity.getShow()));
        if (entity.getShowSeats() != null) {
            ticket.setShowSeats(entity.getShowSeats().stream()
                    .map(EntityMapper::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return ticket;
    }

    public static TicketEntity toEntity(Ticket domain) {
        if (domain == null) return null;
        TicketEntity entity = new TicketEntity();
        entity.setTicketId(domain.getTicketId());
        entity.setShow(toEntity(domain.getShow()));
        if (domain.getShowSeats() != null) {
            entity.setShowSeats(domain.getShowSeats().stream()
                    .map(EntityMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return entity;
    }
}
