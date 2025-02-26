package org.library.thelibraryj.infrastructure.tokenServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
class TokenCleanerService {

    private final List<TokenService> tokenServices;

    TokenCleanerService(){
        this.tokenServices = List.of(

        );
    }

    @Transactional
    @Scheduled(cron = "0 0 0 */${library.token.cleaning_interval_days} * *")
    void clearInvalidTokens() {
        log.info("Clearing token tables for {} services", tokenServices.size());
        tokenServices.forEach(tokenService -> {
            log.info("Clearing token table for service {}", tokenService.getClass().getSimpleName());
            tokenService.clearInvalidTokens();
        });
    }
}
