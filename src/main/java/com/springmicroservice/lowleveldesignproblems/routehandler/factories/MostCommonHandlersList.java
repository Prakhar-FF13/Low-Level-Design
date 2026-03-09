package com.springmicroservice.lowleveldesignproblems.routehandler.factories;

import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.AuthHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.IHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.LogAfterHandler;
import com.springmicroservice.lowleveldesignproblems.routehandler.handlers.LogBeforeHandler;

public class MostCommonHandlersList {
    static IHandler getHandlers() {
        // Logging should probably be before auth handler
        return new AuthHandler(new LogBeforeHandler(new LogAfterHandler(null)));
    }
}
