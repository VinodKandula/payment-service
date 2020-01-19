package com.payment.it;

import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * @author Vinod Kandula
 */
public class ITFundsTransferController extends BaseITTest {

    @Test
    public void accountToAccountFundsTransferShouldSucceed() {
        request().body(transferJson("123456789", "987654321", "1.00", "GBP")).post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(200)
                .body("remitterAccount", is("123456789"))
                .body("beneficiaryAccount", is("987654321"))
                .body("amount.value", is("1.00"))
                .body("amount.currency", is("GBP"))
                .body("status", is("SUCCESS"));
    }

    @Test
    public void accountToAccountFundsTransferShouldFailOnCurrencyConversion() {
        request().body(transferJson("123456789", "987654321", "1.00", "USD")).post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(500)
                .body("message", is("Internal server error"));
    }

    @Test
    public void accountToAccountFundsTransferShouldFailOnInsufficientFunds() {
        request().body(transferJson("123456789", "987654321", "99999.00", "GBP")).post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(400)
                .body("remitterAccount", is("123456789"))
                .body("beneficiaryAccount", is("987654321"))
                .body("amount.value", is("99999.00"))
                .body("amount.currency", is("GBP"))
                .body("status", is("FAIL"));
    }

    @Test
    public void accountToAccountFundsTransferShouldFailOnNoSuchAccount() {
        request().body(transferJson("123", "987654321", "1.00", "GBP")).post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(400)
                .body("message", is("No such account: 123"));
    }

    @Test
    public void accountToAccountFundsTransferMustFailOnBadRestRequestFormat() {
        request().body("{}").post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(400)
                .body("message", is("Invalid transfer request"));

        request().body(transferJson("123456789", "987654321", "-1.00", "GBP")).post(FUNDS_TRANSFER_PATH)
                .then()
                .statusCode(400)
                .body("message", is("Amount must be greater than zero"));
    }

    private String transferJson(String remitter, String beneficiary, String amount, String currency) {
        return "{"
                +     "\"remitterAccount\": \"" + remitter + "\", "
                +     "\"beneficiaryAccount\": \"" + beneficiary + "\", "
                +     "\"amount\": { "
                +         "\"currency\": \"" + currency + "\", "
                +         "\"value\": \"" + amount + "\""
                +     "}"
                + "}";
    }

}
