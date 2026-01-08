package com.finance.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ValidationTest {


    private fun isValidTransactionAmount(amount: Double): Boolean {
        return amount > 0.0
    }


    private fun validateTransactionAmount(amount: Double) {
        if (amount <= 0.0) {
            throw IllegalArgumentException(
                "Transaction amount must be greater than 0"
            )
        }
    }

    @Test
    fun `positive transaction amount should be valid`() {

        val amount = 100.0


        val result = isValidTransactionAmount(amount)


        assertTrue(
            result,
            "Positive transaction amount should be considered valid"
        )
    }

    @Test
    fun `zero transaction amount should be invalid`() {

        val amount = 0.0


        val result = isValidTransactionAmount(amount)


        assertFalse(
            result,
            "Zero transaction amount should be considered invalid"
        )
    }

    @Test
    fun `negative transaction amount should be invalid`() {

        val amount = -50.0


        val result = isValidTransactionAmount(amount)


        assertFalse(
            result,
            "Negative transaction amount should be considered invalid"
        )
    }

    @Test
    fun `validation should throw exception for zero amount`() {

        val amount = 0.0


        val exception = assertThrows<IllegalArgumentException> {
            validateTransactionAmount(amount)
        }

        assertEquals(
            "Transaction amount must be greater than 0",
            exception.message
        )
    }

    @Test
    fun `validation should throw exception for negative amount`() {

        val amount = -100.0


        val exception = assertThrows<IllegalArgumentException> {
            validateTransactionAmount(amount)
        }

        assertEquals(
            "Transaction amount must be greater than 0",
            exception.message
        )
    }

    @Test
    fun `validation should not throw exception for positive amount`() {

        val amount = 250.5


        assertDoesNotThrow {
            validateTransactionAmount(amount)
        }
    }
}
