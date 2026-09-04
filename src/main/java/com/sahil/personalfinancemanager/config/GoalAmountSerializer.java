package com.sahil.personalfinancemanager.config;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GoalAmountSerializer extends ValueSerializer<BigDecimal> {

    @Override
    public void serialize(
            BigDecimal value,
            JsonGenerator gen,
            SerializationContext context
    ) throws JacksonException {

        if (value == null) {
            gen.writeNull();
            return;
        }

        // E2E script expects 0 instead of 0.00
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            gen.writeNumber(0);
            return;
        }

        // Non-zero monetary values keep 2 decimal places
        gen.writeNumber(
                value.setScale(2, RoundingMode.HALF_UP)
        );
    }
}