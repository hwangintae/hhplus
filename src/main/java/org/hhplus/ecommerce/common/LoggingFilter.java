package org.hhplus.ecommerce.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        filterChain.doFilter(requestWrapper, responseWrapper);

        long endTime = System.currentTimeMillis();

        String logMessage = String.format(
                ">>> Method: %s, URI: %s, Time: %.5f seconds%n" +
                ">>>>>> Headers: %s%n" +
                ">>>>>> Query String: %s%n" +
                ">>>>>> Request Body: %s%n" +
                ">>> Response Status: %s, Response Body: %s",
                requestWrapper.getMethod(), requestWrapper.getRequestURI(), (endTime - startTime) / 1000.0,
                getHeaders(requestWrapper),
                requestWrapper.getQueryString(),
                getRequestBody(requestWrapper),
                HttpStatus.valueOf(responseWrapper.getStatus()), getResponseBody(responseWrapper)
        );

        log.info(logMessage);

        responseWrapper.copyBodyToResponse();
    }

    public String getHeaders(ContentCachingRequestWrapper requestWrapper) {

        List<String> list = new ArrayList<>();

        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = requestWrapper.getHeader(headerName);
            list.add(headerName + ": " + headerValue);
        }

        if (list.isEmpty()) return "";

        return String.join(",", list);
    }

    public String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        byte[] content = requestWrapper.getContentAsByteArray();

        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }

        return "";
    }

    public String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] content = responseWrapper.getContentAsByteArray();

        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }

        return "";
    }
}
