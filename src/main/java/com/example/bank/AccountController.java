package com.example.bank;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class AccountController {

    private final AccountRepository repository;
    private final AccountModelAssembler assembler;

    AccountController(AccountRepository repository, AccountModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/accounts")
    CollectionModel<EntityModel<Account>> all(@RequestParam("name") Optional<String> name) {
        var all = repository.findAll().stream()
                .map(assembler::toModel);

        if (name.isPresent()) {
            all = all.filter(account -> account.getContent().getName().equals(name.get()));
        }
        return CollectionModel.of(all.collect(Collectors.toList()),
                linkTo(methodOn(AccountController.class).all(null)).withSelfRel());
    }

    @PostMapping("/accounts")
    ResponseEntity<EntityModel<Account>> newAccount(@RequestBody Account newAccount) {
        EntityModel<Account> entityModel = assembler.toModel(repository.save(newAccount));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/accounts/{id}")
    EntityModel<Account> one(@PathVariable Long id) {

        Account account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return assembler.toModel(account);
    }

    @PutMapping("/accounts/{id}")
    ResponseEntity<EntityModel<Account>> updateOrCreateAccount(@RequestBody Account newAccount, @PathVariable Long id) {

        Account updatedAccount = repository.findById(id)
                .map(account -> {
                    if (newAccount.getName() != null) account.setName(newAccount.getName());
                    if (newAccount.getBalance() != null) {
                        if (account.getBalance().getCurrency() == newAccount.getBalance().getCurrency()) {
                            account.setBalance(newAccount.getBalance());
                        } else {
                            throw new NonMatchingCurrencyException("Cannot change account's currency");
                        }
                    }
                    return repository.save(account);
                })
                .orElseGet(() -> {
                    newAccount.setId(id);
                    return repository.save(newAccount);
                });

        EntityModel<Account> entityModel = assembler.toModel(repository.save(updatedAccount));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/accounts/{id}")
    ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}