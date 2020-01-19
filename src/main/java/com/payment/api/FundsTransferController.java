package com.payment.api;

import com.google.gson.Gson;
import com.payment.dto.FundsTransferDto;
import com.payment.model.FundsTransfer;
import com.payment.service.AccountRepository;
import com.payment.service.FundsTransferService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigDecimal;

import static com.payment.model.FundsTransfer.Status.FAIL;

/**
 * @author Vinod Kandula
 */
public class FundsTransferController implements Route {

    private AccountRepository accountService;
    private FundsTransferService transferService;

    private Gson gson;

    public FundsTransferController(Gson gson, AccountRepository accountService, FundsTransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;

        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        FundsTransferDto transferDTO = gson.fromJson(request.body(), FundsTransferDto.class);
        validate(transferDTO);

        FundsTransfer transfer = transferService.processFundsTransfer(parseToBO(transferDTO));

        response.status(FAIL.equals(transfer.getStatus()) ? 400 : 200);
        response.header("Content-Type", "application/json");

        return gson.toJson(parseToDTO(transfer));
    }

    private void validate(FundsTransferDto transferDTO) {
        if (transferDTO.beneficiaryAccount == null || transferDTO.remitterAccount == null || transferDTO.amount == null) {
            throw new IllegalArgumentException("Invalid transfer request");
        }

        if (transferDTO.amount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private FundsTransfer parseToBO(FundsTransferDto to) {
        FundsTransfer transfer = new FundsTransfer();
        transfer.setRemitter(accountService.findByNumber(to.remitterAccount));
        transfer.setBeneficiary(accountService.findByNumber(to.beneficiaryAccount));
        transfer.setAmount(to.amount);

        return transfer;
    }

    private FundsTransferDto parseToDTO(FundsTransfer transfer) {
        FundsTransferDto transferDTO = new FundsTransferDto();
        transferDTO.beneficiaryAccount = transfer.getBeneficiary().getNumber();
        transferDTO.remitterAccount = transfer.getRemitter().getNumber();
        transferDTO.amount = transfer.getAmount();
        transferDTO.status = transfer.getStatus();

        return transferDTO;
    }
}
