package com.springmicroservice.lowleveldesignproblems.routehandler;

import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.IHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteHandler {
    Map<String, IHandler>  handlers;

    Response serveRequest(Request request) {
        if (request == null) {
            return null;
        }
        IHandler handler = handlers.get(request.getUrl());
        if (handler == null) {
            return null;
        }
        return handler.handle(request);
    }
}
