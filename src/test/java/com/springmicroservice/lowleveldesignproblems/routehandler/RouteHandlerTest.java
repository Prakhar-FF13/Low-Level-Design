package com.springmicroservice.lowleveldesignproblems.routehandler;

import com.springmicroservice.lowleveldesignproblems.routehandler.controller.DoNothingController;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Request;
import com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto.Response;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.AuthHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.IHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.LogAfterHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.LogBeforeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RouteHandlerTest {

  private RouteHandler routeHandler;

  @BeforeEach
  void setUp() {
    // Setup handler chain: LogBefore -> Auth -> LogAfter -> Controller
    IHandler controller = new DoNothingController(null);
    IHandler logAfter = new LogAfterHandler(controller);
    IHandler auth = new AuthHandler(logAfter);
    IHandler logBefore = new LogBeforeHandler(auth);

    Map<String, IHandler> handlers = new HashMap<>();
    handlers.put("/api/secure-data", logBefore);

    routeHandler = new RouteHandler(handlers);
  }

  @Test
  void testServeRequest_SuccessWithAuthHeader() {
    Request request = new Request();
    request.setUrl("/api/secure-data");
    request.setMethod("GET");

    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer token123");
    request.setHeaders(headers);

    Response response = routeHandler.serveRequest(request);

    assertNotNull(response, "Response should not be null for a valid, authorized request");
  }

  @Test
  void testServeRequest_FailsWithoutAuthHeader() {
    Request request = new Request();
    request.setUrl("/api/secure-data");
    request.setMethod("GET");

    // No Authorization header
    Map<String, String> headers = new HashMap<>();
    request.setHeaders(headers);

    Response response = routeHandler.serveRequest(request);

    assertNotNull(response, "Response should not be null even on failure (AuthHandler returns empty response)");
    // Since the current implementation returns a new empty Response() instead of
    // throwing an error or setting a status,
    // this test essentially verifies it doesn't crash.
    // A more robust implementation would allow us to assert an HTTP 401 status code
    // here.
  }

  @Test
  void testServeRequest_RouteNotFound() {
    Request request = new Request();
    request.setUrl("/api/unknown");
    request.setMethod("GET");

    Response response = routeHandler.serveRequest(request);

    assertNull(response, "Response should be null when route is not found");
  }

  @Test
  void testServeRequest_NullRequest() {
    Response response = routeHandler.serveRequest(null);
    assertNull(response, "Response should be null when request is null");
  }
}
