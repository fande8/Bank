package com.example.bank;

class TransferNotFoundException extends RuntimeException {

    TransferNotFoundException(Long id) {
        super("Transfer with id: " + id + " does not exist");
    }
}