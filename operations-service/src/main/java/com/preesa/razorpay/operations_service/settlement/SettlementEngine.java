package com.preesa.razorpay.operations_service.settlement;


import com.preesa.razorpay.operations_service.client.MerchantServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j 
@Component 
@RequiredArgsConstructor 
public class SettlementEngine {

    private final MerchantServiceClient merchantServiceClient;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(cron = "0 0 23 * * *")
    @SchedulerLock (name = "operations-service-settlement-engine", lockAtMostFor = "2h", lockAtLeastFor = "1s" )
    public void runScheduled() {
        log.info("Nightly settlement Scheduler running");
        run();
    }

    private void run() {

        List<UUID> merchantIds = merchantServiceClient.listActiveMerchantIds();
        log.info("Processing the settlement for active merchants : {}", merchantIds.size());

        try (ExecutorService virtualExecutorService = Executors.newVirtualThreadPerTaskExecutor()) {

            List<Future<?>> futures = new ArrayList<>();

            for (UUID id : merchantIds) {
                virtualExecutorService.submit(() -> {
                  settlementTransactionExecutor.processForMerchant(id, Instant.now());
                });
            }

            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {

                    log.error("settlement batch future failed");
                    throw new RuntimeException(e);
                }
            }

        }
        log.info("Settlement batch completed");

    }

}
