package com.springmicroservice.lowleveldesignproblems.bookmyshow.api.controller;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.api.dto.BookingRequest;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.api.dto.BookingResponse;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.api.dto.AvailableSeatResponse;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.application.booking.BookingService;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.BookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.ConcurrentBookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/shows/{showId}/available-seats")
    public ResponseEntity<List<AvailableSeatResponse>> getAvailableSeats(@PathVariable Long showId) {
        var seats = bookingService.getAvailableSeats(showId);
        var response = seats.stream()
                .map(this::toSeatResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> bookSeats(@Valid @RequestBody BookingRequest request) {
        Ticket ticket = bookingService.bookSeats(request.showId(), request.seatIds());
        var response = toResponse(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(ConcurrentBookingException.class)
    public ResponseEntity<String> handleConcurrentBookingException(ConcurrentBookingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<String> handleBookingException(BookingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    private AvailableSeatResponse toSeatResponse(ShowSeats showSeats) {
        return new AvailableSeatResponse(
                showSeats.getId(),
                showSeats.getSeat().getId(),
                showSeats.getSeat().getSeatNumber(),
                showSeats.getSeat().getRow(),
                showSeats.getSeat().getColumn()
        );
    }

    private BookingResponse toResponse(Ticket ticket) {
        List<Long> seatIds = ticket.getShowSeats().stream()
                .map(ss -> ss.getSeat().getId())
                .toList();
        return new BookingResponse(
                ticket.getTicketId(),
                ticket.getShow().getShowId(),
                seatIds
        );
    }
}
