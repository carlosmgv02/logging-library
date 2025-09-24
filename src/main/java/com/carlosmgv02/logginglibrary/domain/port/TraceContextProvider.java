package com.carlosmgv02.logginglibrary.domain.port;

import java.util.Optional;

public interface TraceContextProvider {
    Optional<String> getCurrentTraceId();
    Optional<String> getCurrentSpanId();
    boolean isTraceActive();
}