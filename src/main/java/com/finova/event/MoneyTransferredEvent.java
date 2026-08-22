package com.finova.event;

import com.finova.entity.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

@Getter
public class MoneyTransferredEvent extends ApplicationEvent {
    private final Account sender;
    private final Account receiver;
    private final BigDecimal amount;

    public MoneyTransferredEvent(Object source, Account sender, Account receiver, BigDecimal amount) {
        super(source);
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
}