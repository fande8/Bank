package com.example.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@SpringBootTest
class TransferControllerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    private TransferController transferController;

    @PostConstruct
    void TransferControllerTest() {
        this.transferController = new TransferController(transferRepository, accountRepository);
    }

    @Test
    void successfulTransfer() {
        Transfer transfer = new Transfer(Long.valueOf(1), Long.valueOf(2), 10);
        var transferModel = transferController.newTransfer(transfer);
        HttpStatus resultStatus = transferController.completeTransfer(transferModel.getContent().getId()).getStatusCode();
        Assertions.assertEquals(resultStatus, HttpStatus.CREATED);
        Assertions.assertEquals(0, accountRepository.findById(transfer.getSource()).get().getBalance().getAmount());
        Assertions.assertEquals(30, accountRepository.findById(transfer.getDestination()).get().getBalance().getAmount());
    }

    @Test
    void negativeBalance() {
        Transfer transfer = new Transfer(Long.valueOf(2), Long.valueOf(1), 100);
        var transferModel = transferController.newTransfer(transfer);
        HttpStatus resultStatus = transferController.completeTransfer(transferModel.getContent().getId()).getStatusCode();
        Assertions.assertEquals(resultStatus, HttpStatus.FORBIDDEN);
    }

    @Test
    void differentCurrency() {
        Transfer transfer = new Transfer(Long.valueOf(3), Long.valueOf(1), 100);
        var transferModel = transferController.newTransfer(transfer);
        Assertions.assertThrows(NonMatchingCurrencyException.class,
                () -> transferController.completeTransfer(transferModel.getContent().getId()));
    }
}