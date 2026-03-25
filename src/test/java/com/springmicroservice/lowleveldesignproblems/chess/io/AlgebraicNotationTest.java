package com.springmicroservice.lowleveldesignproblems.chess.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Move;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

class AlgebraicNotationTest {

    @Test
    void parseSquare_mapsE4ToBoardCoordinates() {
        assertThat(AlgebraicNotation.parseSquare("e4")).contains(new Square(4, 4));
    }

    @Test
    void parseMove_acceptsTwoTokens() {
        var move = AlgebraicNotation.parseMove("e2 e4");
        assertThat(move).contains(new Move(new Square(6, 4), new Square(4, 4)));
    }

    @Test
    void parseMove_acceptsCompactFourChars() {
        var move = AlgebraicNotation.parseMove("e2e4");
        assertThat(move).contains(new Move(new Square(6, 4), new Square(4, 4)));
    }

    @Test
    void format_roundTripsWithParseSquare() {
        Square square = new Square(6, 4);
        assertThat(AlgebraicNotation.parseSquare(AlgebraicNotation.format(square)))
                .contains(square);
    }
}
