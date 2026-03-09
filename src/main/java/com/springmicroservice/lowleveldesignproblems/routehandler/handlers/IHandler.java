package com.springmicroservice.lowleveldesignproblems.routehandler.handlers;

import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;

public interface IHandler {
    Response handle(Request request);
}
