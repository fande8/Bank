package com.example.bank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(AccountRepository repository) {
        return args -> {
            repository.save(new Account("test1", new Money(Currency.USD, 10), true));
            repository.save(new Account("test2", new Money(Currency.USD, 20), false));
            repository.save(new Account("test3", new Money(Currency.EUR, 20), false));
        };
    }
}