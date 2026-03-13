package com.springmicroservice.lowleveldesignproblems.cricbuzz.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ICCAPI implements ScoreService {
    private int runs;
    private int wickets;
    private int oversRemaining;
}
