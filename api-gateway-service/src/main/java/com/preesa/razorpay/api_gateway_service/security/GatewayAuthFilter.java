package com.preesa.razorpay.api_gateway_service.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.preesa.razorpay.common_lib.exceptions.GatewayAuthenticationException;
import com.preesa.razorpay.common_lib.exceptions.RateLimitException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j 
@Order(Ordered.HIGHEST_PRECEDENCE+1)
@Component 
@RequiredArgsConstructor 
public class GatewayAuthFilter extends OncePerRequestFilter {

    private final static  String BASIC_PREFIX = "Basic ";
    private final static  String BEARER_PREFIX = "Bearer ";

    private final JwtAuthHandler jwtAuthHandler;
    private final ApiKeyAuthHandler apiKeyAuthHandler;
    private final PublicRouteMatcher publicRouteMatcher;
    private  final ObjectMapper objectMapper;



@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

            log.info("Incomming Request "+ request.getRequestURI());
            if(publicRouteMatcher.isPublic(request.getRequestURI())){
                 log.info("Incomming Request "+ request.getRequestURI() + " is public");
                filterChain.doFilter(request, response);
                return ;
            }

            String authorizationHeader = request.getHeader("Authorization");

            try{
            Map<String,String> extraHeaders = Map.of();

            if(authorizationHeader.startsWith(BASIC_PREFIX)){
               extraHeaders= apiKeyAuthHandler.authenticate(authorizationHeader, response);
            }
            else if (authorizationHeader.startsWith(BEARER_PREFIX)){
                extraHeaders = jwtAuthHandler.authenticate(authorizationHeader.substring(BEARER_PREFIX.length()));
            }
            else{
                 reject(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", " Missing or Invalid authentication header ");
                 return;
            }
            HeaderAugmentingRequestWrapper wrapper = new HeaderAugmentingRequestWrapper(request);

            extraHeaders.forEach((x,y)-> wrapper.putHeader(x, y));

            filterChain.doFilter(wrapper,response);

            }
            catch (RateLimitException e) {
            response.setHeader("Retry-After", String.valueOf(e.getRetryAfterSeconds()));
            reject(response, HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", e.getMessage());
        } catch (GatewayAuthenticationException e) {
            reject(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage());
        } catch (Exception e) {
            log.warn("Gateway auth failed for path={}", request.getRequestURI(), e);
            reject(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid credentials");
        }

    }

    private void reject(HttpServletResponse response, HttpStatus status, String errorCode, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of("errorCode", errorCode, "errorDescription", message));
    }
}
