package com.example.unittestingbasics

class MyCalculation : Calculation {

    override fun sum(operand1: Int, operand2: Int): Int {
        return operand1 + operand2
    }

    override fun multiply(operand1: Int, operand2: Int): Int {
        return operand1 * operand2
    }

    override fun sumAndSquare(operand1: Int, operand2: Int): Int {
        val sum = sum(operand1, operand2)
        return multiply(sum, sum)
    }
}