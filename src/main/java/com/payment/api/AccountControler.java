package com.payment.api;

import com.google.gson.Gson;
import com.payment.model.Account;
import com.payment.service.AccountRepository;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

/**
 * @author Vinod Kandula
 */
public class AccountControler implements Route {
    private AccountRepository accountService;
    private Gson gson;

    public AccountControler(Gson gson, AccountRepository accountService) {
        this.gson = gson;
        this.accountService = accountService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        List<Account> accountList = accountService.findAll();
        return gson.toJson(accountList);
    }
}
