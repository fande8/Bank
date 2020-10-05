package com.example.bank;

class NonMatchingCurrencyException extends RuntimeException {

    NonMatchingCurrencyException(String message) { super(message); }
}