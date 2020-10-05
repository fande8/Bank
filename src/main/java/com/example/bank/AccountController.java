package com.example.bank;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
class AccountController {

    private final AccountRepository repository;

    AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/accounts")
    Iterable<Account> all(@RequestParam("name") Optional<String> name) {
        var all = repository.findAll();
        if (name.isPresent()) {
            all = all.stream().filter(account -> account.getName().equals(name.get())).collect(Collectors.toList());
        }
        return all;
    }

    @PostMapping("/accounts")
    Account newAccount(@RequestBody Account newAccount) {
        return repository.save(newAccount);
    }

    @GetMapping("/accounts/{id}")
    Account one(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @PutMapping("/accounts/{id}")
    Account updateOrCreateAccount(@RequestBody Account newAccount, @PathVariable Long id) {

        return repository.findById(id)
                .map(account -> {
                    if(newAccount.getName() != null) account.setName(newAccount.getName());
                    if(newAccount.getBalance() != null) account.setBalance(newAccount.getBalance());
                    return repository.save(account);
                })
                .orElseGet(() -> {
                    newAccount.setId(id);
                    return repository.save(newAccount);
                });
    }

    @DeleteMapping("/accounts/{id}")
    void deleteAccount(@PathVariable Long id) {
        repository.deleteById(id);
    }
}