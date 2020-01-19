package com.payment.util;

import com.google.gson.*;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @author Vinod Kandula
 */
public class MoneyDeserializer implements JsonDeserializer<Money> {
    @Override
    public Money deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return Money.of(
                CurrencyUnit.of(jsonObject.get("currency").getAsString()),
                new BigDecimal(jsonObject.get("value").getAsString())
        );
    }
}
