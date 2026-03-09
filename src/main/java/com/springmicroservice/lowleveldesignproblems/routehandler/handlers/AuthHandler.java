package com.springmicroservice.lowleveldesignproblems.routehandler.handlers;

import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthHandler implements IHandler {
    private IHandler next;

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equalsIgnoreCase("GET") &&
                request.getHeaders().containsKey("Authorization")
        ) {
            if (next != null) {
                return next.handle(request);
            }
        }
        return new Response();
    }
}
