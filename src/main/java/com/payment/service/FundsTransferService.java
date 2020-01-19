package com.payment.service;

import com.payment.exception.InsufficientBalanceException;
import com.payment.model.Account;
import com.payment.model.FundsTransfer;
import org.joda.money.Money;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import static com.payment.model.FundsTransfer.Status.FAIL;
import static com.payment.model.FundsTransfer.Status.SUCCESS;

/**
 * @author Vinod Kandula
 *
 * The {@link FundsTransferService} is reponsible for transferring money from one account to another account
 */
public class FundsTransferService extends BaseDaoRepository {

    public FundsTransferService(EntityManagerFactory factory) {
        super(factory);
    }

    /**
     * Transfers the given amount from one account to another.
     * @param fundsTransfer
     * @return {@link FundsTransfer}
     */
    public FundsTransfer processFundsTransfer(FundsTransfer fundsTransfer) {
        return executeInTransaction(entityManager -> {
            Account remitterAccount = fundsTransfer.getRemitter();
            Account beneficiaryAccount = fundsTransfer.getBeneficiary();

            // acquiring the account locks in same order to avoid deadlock
            if (remitterAccount.getNumber().compareTo(beneficiaryAccount.getNumber()) < 0) {
                remitterAccount = acquireLockAccount(entityManager, remitterAccount);
                beneficiaryAccount = acquireLockAccount(entityManager, beneficiaryAccount);
            } else {
                beneficiaryAccount = acquireLockAccount(entityManager, beneficiaryAccount);
                remitterAccount = acquireLockAccount(entityManager, remitterAccount);
            }

            try {
                validateFundsTransfer(remitterAccount, beneficiaryAccount, fundsTransfer.getAmount());
            } catch (InsufficientBalanceException e) {
                fundsTransfer.setStatus(FAIL);
                return fundsTransfer;
            }

            transferMoney(remitterAccount, beneficiaryAccount, fundsTransfer.getAmount());

            entityManager.merge(remitterAccount);
            entityManager.merge(beneficiaryAccount);

            fundsTransfer.setStatus(SUCCESS);

            return fundsTransfer;
        });
    }

    private Account acquireLockAccount(EntityManager manager, Account account) {
        return manager.find(Account.class, account.getId(), LockModeType.PESSIMISTIC_WRITE);
    }

    private void validateFundsTransfer(Account remitter, Account beneficiary, Money amount) {
        Money remitterBalance = remitter.getBalance();
        Money beneficiaryBalance = beneficiary.getBalance();

        if (!remitterBalance.getCurrencyUnit().equals(beneficiaryBalance.getCurrencyUnit())
                || !remitterBalance.getCurrencyUnit().equals(amount.getCurrencyUnit())) {
            throw new UnsupportedOperationException("Currency conversion not supported yet");
        }

        if (remitterBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds");
        }
    }

    private void transferMoney(Account remitterAccount, Account beneficiaryAccount, Money amount) {
        Money remitterBalance = remitterAccount.getBalance();
        Money beneficiaryBalance = beneficiaryAccount.getBalance();

        remitterAccount.setBalance(remitterBalance.minus(amount));
        beneficiaryAccount.setBalance(beneficiaryBalance.plus(amount));
    }

}
