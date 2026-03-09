package com.springmicroservice.lowleveldesignproblems.routehandler.handlers;

import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogAfterHandler implements IHandler{
    private IHandler next;

    @Override
    public Response handle(Request request) {
        if (next == null) {
            return new Response();
        }
        Response response = next.handle(request);
        System.out.println("LogAfterHandler.handle");
        return response;
    }
}
