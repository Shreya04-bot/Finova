package com.finova.event;

import com.finova.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleMoneyTransferred(MoneyTransferredEvent event) {
        notificationService.createNotification(
                event.getSender().getUser(),
                "Money Transfer Successful",
                "₹" + event.getAmount() + " sent to " + event.getReceiver().getAccountNumber() + " successfully."
        );
        notificationService.createNotification(
                event.getReceiver().getUser(),
                "Money Received",
                "₹" + event.getAmount() + " received from " + event.getSender().getAccountNumber() + "."
        );
    }

    @Async
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        notificationService.createNotification(
                event.getAccount().getUser(),
                "Payment Successful",
                "₹" + event.getAmount() + " payment to " + event.getPayee() + " was successful."
        );
    }
}