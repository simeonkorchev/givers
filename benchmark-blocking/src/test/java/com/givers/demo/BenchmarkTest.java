package com.givers.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BenchmarkTest {
    private static final int PARALLELISM = 96;
    
    private final String BASE_URL = "http://localhost:8089";
    
    @Test
    public void benchmarkOneRequestBlocking() throws Exception {
        runBenchmark(1, PARALLELISM, "/causes-blocking");
    }
    
    @Test
    public void benchmark8RequestBlocking() throws Exception {
        runBenchmark(8, PARALLELISM, "/causes-blocking");
    }
    
    @Test
    public void benchmark32RequestBlocking() throws Exception {
        runBenchmark(32, PARALLELISM, "/causes-blocking");
    }
    
    @Test
    public void benchmark96RequestBlocking() throws Exception {
        runBenchmark(96, PARALLELISM, "/causes-blocking");
    }
  
    @Test
    public void benchmark768RequestBlocking() throws Exception {
        runBenchmark(768, PARALLELISM, "/causes-blocking");
    }
   
    private void runBenchmark(final int requests, final int parallelism, final String context) throws Exception {
        long start = System.nanoTime();

        WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();
        
        HashMap<Integer, BenchmarkRequestResult> results = new HashMap<>();

        List<Callable<BenchmarkRequestResult>> requestCallableList = IntStream.range(0, requests)
                .mapToObj(i -> createMonoRequest(i, webClient, context))
                .collect(Collectors.toList());

        log.info(" ========== NEW BENCHMARK --> Requests: {}, Parallelism: {}, URL: {}", requests, parallelism, context);
        log.info(" ========== Requests created ");

        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
        ExecutorCompletionService<BenchmarkRequestResult> completionService = new ExecutorCompletionService<>(executorService);
        requestCallableList.forEach(completionService::submit);

        log.info(" ========== Requests submitted @ {}", Duration.ofNanos(System.nanoTime() - start));

        for (int n = 0; n < requestCallableList.size(); n++) {
            BenchmarkRequestResult benchmarkRequestResult = completionService.take().get();
            results.put(benchmarkRequestResult.getRequestId(), benchmarkRequestResult);
        }

        log.info(" ========== Requests completed @ {}", Duration.ofNanos(System.nanoTime() - start));

        log.info(" ========== RESULTS ========== ");
        double avg = results.values().stream().mapToLong(r -> r.getTookTimeNs()).average().getAsDouble();
        log.info("Average time per request: {}", Duration.ofNanos(Math.round(avg)));
        double successRate = results.values().stream().
                filter(r -> r.getResponseEntity().getStatusCode().equals(HttpStatus.OK)).count() * 100.0 /
                results.size();
        double errorRate = 100.0 - successRate;
        log.info("Success Rate: {}", successRate);
        log.info("Error Rate:   {}", errorRate);
        int nCauses = results.values().stream().map(r -> r.getResponseEntity().getBody()).mapToInt(this::countCauses).sum();
        log.info("Total Number of Causes: {}", nCauses);

        long end = System.nanoTime();
        log.info(" ========== Benchmark took {} ", Duration.ofNanos(end - start));

        assertThat(nCauses).as("Number of total Received Causes must be equal to the number " +
                "of requests times 10 (results per page)").isEqualTo(requests * 10);
    }

    private Callable<BenchmarkRequestResult> createMonoRequest(final int requestId, final WebClient webClient, final String url) {
        return () -> {
            long start = System.nanoTime();
            ResponseEntity<String> responseEntity = webClient.get().uri(url).exchange().block().toEntity(String.class).block();
            long end = System.nanoTime();
            return new BenchmarkRequestResult(requestId, responseEntity, end - start);
        };
    }

    private int countCauses(final String s) {
        return StringUtils.countOccurrencesOf(s, "\"id\"");
    }
}
