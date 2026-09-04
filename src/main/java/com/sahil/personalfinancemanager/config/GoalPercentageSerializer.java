package com.sahil.personalfinancemanager.config;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GoalPercentageSerializer extends ValueSerializer<BigDecimal> {

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

        BigDecimal normalized = value.stripTrailingZeros();

        /*
         * Required by the E2E script:
         *
         * 0.00  -> 0.0
         * 50.00 -> 50.0
         * 65.50 -> 65.5
         * 60.00 -> 60.0
         * 60.33 -> 60.33
         */

        if (normalized.scale() <= 1) {
            normalized = normalized.setScale(
                    1,
                    RoundingMode.HALF_UP
            );
        } else {
            normalized = value.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        gen.writeNumber(normalized);
    }
}