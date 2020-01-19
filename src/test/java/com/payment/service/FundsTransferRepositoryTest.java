package com.payment.service;

import com.payment.PaymentApp;
import com.payment.model.FundsTransfer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.payment.model.FundsTransfer.Status.FAIL;
import static com.payment.model.FundsTransfer.Status.SUCCESS;
import static org.junit.Assert.assertEquals;

/**
 * @author Vinod Kandula
 */
public class FundsTransferRepositoryTest {

    private static final String ACCOUNT_1 = "123456789";
    private static final String ACCOUNT_2 = "987654321";

    private FundsTransferService fundsTransferRepository;
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        PaymentApp application = new PaymentApp();
        application.init();

        fundsTransferRepository = application.getFundsTransferRepository();
        accountRepository = application.getAccountRepository();
    }

    @Test
    public void accountBalancesShouldBeChangedCorrectly() {
        // given
        Money firstInitialBalance = getFirstAccountBalance();
        Money secondInitialBalance = getSecondAccountBalance();

        // when
        FundsTransfer transfer = fundsTransferRepository.processFundsTransfer(newTransfer(Money.of(CurrencyUnit.GBP, 10)));

        // then
        assertEquals(SUCCESS, transfer.getStatus());

        assertEquals(new BigDecimal("10.00"), firstInitialBalance.minus(getFirstAccountBalance()).getAmount());
        assertEquals(new BigDecimal("10.00"), getSecondAccountBalance().minus(secondInitialBalance).getAmount());
    }

    @Test
    public void accountBalancesMustNotBeChangedOnException() {
        // given
        Money firstInitialBalance = getFirstAccountBalance();
        Money secondInitialBalance = getSecondAccountBalance();

        // when
        FundsTransfer transfer = fundsTransferRepository.processFundsTransfer(newTransfer(Money.of(CurrencyUnit.GBP, 99999)));

        // then
        assertEquals(FAIL, transfer.getStatus());

        assertEquals(firstInitialBalance, getFirstAccountBalance());
        assertEquals(secondInitialBalance, getSecondAccountBalance());
    }

    @Test
    public void concurrentFundsTransfersMustBeExecutedSuccessfully() {
        // given
        Money firstInitialBalance = getFirstAccountBalance();
        Money secondInitialBalance = getSecondAccountBalance();

        Money transferAmount = Money.of(CurrencyUnit.GBP, 1);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // when
        List<Future> transfers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            transfers.add(executor.submit(() -> fundsTransferRepository.processFundsTransfer(newTransfer(transferAmount))));
            transfers.add(executor.submit(() -> fundsTransferRepository.processFundsTransfer(newBackwardTransfer(transferAmount))));
        }

        for (Future task: transfers) {
            try {
                task.get();
            } catch (Exception e) { /* continue waiting for others on any exception */ }
        }

        executor.shutdownNow();

        // then
        assertEquals(firstInitialBalance, getFirstAccountBalance());
        assertEquals(secondInitialBalance, getSecondAccountBalance());
    }

    private Money getFirstAccountBalance() {
        return accountRepository.findByNumber(ACCOUNT_1).getBalance();
    }

    private Money getSecondAccountBalance() {
        return accountRepository.findByNumber(ACCOUNT_2).getBalance();
    }

    private FundsTransfer newTransfer(Money amount) {
        FundsTransfer transfer = new FundsTransfer();
        transfer.setRemitter(accountRepository.findByNumber(ACCOUNT_1));
        transfer.setBeneficiary(accountRepository.findByNumber(ACCOUNT_2));
        transfer.setAmount(amount);

        return transfer;
    }

    private FundsTransfer newBackwardTransfer(Money amount) {
        FundsTransfer transfer = new FundsTransfer();
        transfer.setRemitter(accountRepository.findByNumber(ACCOUNT_2));
        transfer.setBeneficiary(accountRepository.findByNumber(ACCOUNT_1));
        transfer.setAmount(amount);

        return transfer;
    }
}
