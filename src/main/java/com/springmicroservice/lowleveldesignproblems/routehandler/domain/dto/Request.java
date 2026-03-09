package com.springmicroservice.lowleveldesignproblems.routehandler.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    String url;
    String method;
    String body;
    Map<String, String> headers;
    Map<String, String> pathParameters;
    Map<String, String> queryParameters;
}
