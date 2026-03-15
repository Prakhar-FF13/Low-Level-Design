package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    private Long id;
    private String address;
    private List<Screen> screens = new ArrayList<>();
}
