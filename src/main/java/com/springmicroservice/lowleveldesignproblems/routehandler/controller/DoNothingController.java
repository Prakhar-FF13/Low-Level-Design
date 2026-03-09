package com.springmicroservice.lowleveldesignproblems.routehandler.controller;

import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.IHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoNothingController implements IHandler {
    IHandler handlers;

    @Override
    public Response handle(Request request) {
        return new Response();
    }
}
