package com.spring.reactive.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.annotation.security.RunAs;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest
public class FluxAndMonoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void returnFlux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    void returnFlux_approach2() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void returnFlux_approach3() {
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> listEntityExchangeResult =
                webTestClient.get().uri("/flux")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(Integer.class)
                        .returnResult();
        assertEquals(expectedIntegerList, listEntityExchangeResult.getResponseBody());
    }

    @Test
    void returnFlux_approach4() {
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedIntegerList, response.getResponseBody());
                });
    }

    @Test
    public void returnFluxStream() {
        Flux<Long> longStreamFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(3L)
                .thenCancel()
                .verify();
    }

    @Test
    void returnMono() {
        Integer expectedValue = new Integer(1);
        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response)->{
                   assertEquals(expectedValue,response.getResponseBody());
                });
    }
}