package com.finova.event;

import com.finova.entity.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {
    private final Account account;
    private final String payee;
    private final BigDecimal amount;

    public PaymentSuccessEvent(Object source, Account account, String payee, BigDecimal amount) {
        super(source);
        this.account = account;
        this.payee = payee;
        this.amount = amount;
    }
}