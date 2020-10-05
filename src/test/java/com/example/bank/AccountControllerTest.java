package com.example.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static com.example.bank.Currency.EUR;
import static com.example.bank.Currency.USD;

@SpringBootTest
class AccountControllerTest {

    private final AccountController accountController;

    @Autowired
    AccountControllerTest(AccountRepository repository, AccountModelAssembler assembler) {
        this.accountController = new AccountController(repository, assembler);
    }

    @Test
    void treasuryReassign() {
        Account newAccount = new Account("new", new Money(USD, 10), true);
        var accountModel = accountController.updateOrCreateAccount(newAccount, Long.valueOf(2)).getBody();
        Assertions.assertEquals(false, accountModel.getContent().getTreasury());
    }

    @Test
    void currencyReassign() {
        Account newAccount = new Account("new", new Money(EUR, 10), true);
        Assertions.assertThrows(NonMatchingCurrencyException.class,
                () -> accountController.updateOrCreateAccount(newAccount, Long.valueOf(1)));
    }
}