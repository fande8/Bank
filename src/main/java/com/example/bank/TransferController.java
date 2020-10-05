package com.example.bank;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class TransferController {

    private final TransferRepository transfers;
    private final AccountRepository accounts;

    TransferController(TransferRepository transfers, AccountRepository accounts) {
        this.transfers = transfers;
        this.accounts = accounts;
    }

    @PostMapping("/transfers")
    EntityModel<Transfer> newTransfer(@RequestBody Transfer newTransfer) {
        Transfer savedTransfer = transfers.save(newTransfer);
        return EntityModel.of(savedTransfer,
                linkTo(methodOn(TransferController.class).completeTransfer(savedTransfer.getId())).withRel("complete"));
    }

    @PutMapping("/transfers/{id}")
    ResponseEntity<?> completeTransfer(@PathVariable Long id) {
        synchronized (this.getClass()) {
            Transfer transfer = transfers.findById(id).orElseThrow(() -> new TransferNotFoundException(id));

            if (transfer.getState() == State.Completed) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Problem.create()
                                .withTitle("Bad Request")
                                .withDetail("Transaction has already been completed"));
            } else {
                Account source = accounts.findById(transfer.getSource())
                        .orElseThrow(() -> new TransferNotFoundException(transfer.getSource()));
                Account destination = accounts.findById(transfer.getDestination())
                        .orElseThrow(() -> new TransferNotFoundException(transfer.getDestination()));

                if (source.getBalance().getCurrency() != destination.getBalance().getCurrency())
                    throw new NonMatchingCurrencyException("Cannot transfer money between accounts with different currencies");

                if (!source.getTreasury() && (source.getBalance().getAmount() < transfer.getAmount())) {
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(Problem.create()
                                    .withTitle("Forbidden")
                                    .withDetail("Having negative balance on a non-treasury account is forbidden"));
                }

                source.getBalance().setAmount(source.getBalance().getAmount() - transfer.getAmount());
                destination.getBalance().setAmount(destination.getBalance().getAmount() + transfer.getAmount());
                accounts.save(source);
                accounts.save(destination);
                transfer.setState(State.Completed);
                transfers.save(transfer);

                EntityModel<Transfer> entityModel = EntityModel.of(transfer,
                        linkTo(methodOn(TransferController.class).completeTransfer(id)).withSelfRel());

                return ResponseEntity
                        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                        .body(entityModel);
            }
        }
    }
}