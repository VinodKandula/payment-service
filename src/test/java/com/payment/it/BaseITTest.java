package com.payment.it;

import com.payment.config.AppConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * @author Vinod Kandula
 */
public class BaseITTest {

    protected final String FUNDS_TRANSFER_PATH = AppConfig.FUNDS_TRANSFER_PATH;

    protected RequestSpecification request() {
        return given().port(AppConfig.SERVER_PORT);
    }

}
