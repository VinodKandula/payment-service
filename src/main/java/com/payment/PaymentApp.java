package com.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.api.AccountControler;
import com.payment.api.FundsTransferController;
import com.payment.dto.ErrorDto;
import com.payment.exception.AccountNoFoundException;
import com.payment.exception.InsufficientBalanceException;
import com.payment.model.Account;
import com.payment.service.AccountRepository;
import com.payment.service.FundsTransferService;
import com.payment.util.MoneyDeserializer;
import com.payment.util.MoneySerializer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static com.payment.config.AppConfig.*;
import static spark.Spark.*;

/**
 * @author Vinod Kandula
 */
public class PaymentApp {
    private EntityManagerFactory entityManagerFactory;

    private AccountRepository accountRepository;
    private FundsTransferService fundsTransferRepository;

    private Gson gson;
    private FundsTransferController fundsTransferController;
    private AccountControler accountControler;

    public static void main(String[] args) {
        PaymentApp application = new PaymentApp();
        application.init();
        port(SERVER_PORT);
        application.startWebServer();
    }

    public void init() {
        initDBEmulator();
        initApplication();
        initTestData();
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public FundsTransferService getFundsTransferRepository() {
        return fundsTransferRepository;
    }

    public void startWebServer() {
        post(FUNDS_TRANSFER_PATH, fundsTransferController);

        get(ACCOUNTS_PATH, accountControler);

        exception(Exception.class, (e, request, response) -> {
            response.header("Content-Type", "application/json");
            int errorCode;
            String errorMessage;
            if (e instanceof IllegalArgumentException || e instanceof AccountNoFoundException || e instanceof InsufficientBalanceException) {
                errorCode = 400;
                errorMessage = e.getMessage();
            } else {
                errorCode = 500;
                errorMessage = "Internal server error";
            }
            response.status(errorCode);
            response.body(gson.toJson(
                    new ErrorDto(errorMessage)
            ));
        });
    }

    private void initDBEmulator() {
        entityManagerFactory = Persistence.createEntityManagerFactory("payment-db");
        Runtime.getRuntime().addShutdownHook(new Thread(entityManagerFactory::close));
    }

    private void initApplication() {
        accountRepository = new AccountRepository(entityManagerFactory);
        fundsTransferRepository = new FundsTransferService(entityManagerFactory);

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Money.class, new MoneySerializer())
                .registerTypeAdapter(Money.class, new MoneyDeserializer())
                .create();

        fundsTransferController = new FundsTransferController(gson, accountRepository, fundsTransferRepository);
        accountControler = new AccountControler(gson, accountRepository);
    }

    private void initTestData() {
        Account account1 = new Account();
        account1.setNumber("123456789");
        account1.setBalance(Money.of(CurrencyUnit.GBP, 100));

        Account account2 = new Account();
        account2.setNumber("987654321");
        account2.setBalance(Money.of(CurrencyUnit.GBP, 150));

        accountRepository.save(account1);
        accountRepository.save(account2);
    }
}
