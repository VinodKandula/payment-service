package com.payment.service;

import com.payment.exception.AccountNoFoundException;
import com.payment.model.Account;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * @author Vinod Kandula
 */
public class AccountRepository extends BaseDaoRepository {

    private static final String ACCOUNT_BY_NUMBER_SELECT = "select a from Account a where a.number = :number";

    private static final String ACCOUNT_ALL_SELECT = "select a from Account a ";

    public AccountRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public Account save(Account account) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(account);
            return account;
        });
    }

    @SuppressWarnings("unchecked")
    public Account findByNumber(String number) {
        Account account = executeQuery(entityManager -> (Account) entityManager
                .createQuery(ACCOUNT_BY_NUMBER_SELECT)
                .setParameter("number", number)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null));

        if (account == null) {
            throw new AccountNoFoundException("No such account: " + number);
        }

        return account;
    }

    @SuppressWarnings("unchecked")
    public List<Account> findAll() {
        List<Account> accounts = (List<Account>) executeQuery(entityManager -> (List<Account>) entityManager
                .createQuery(ACCOUNT_ALL_SELECT)
                .getResultList());

        return accounts;
    }

}
