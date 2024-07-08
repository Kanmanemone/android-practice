package com.example.testingviewmodel

interface Calculation {

    fun sum(operand1: Int, operand2: Int): Int

    fun multiply(operand1: Int, operand2: Int): Int

    fun sumAndSquare(operand1: Int, operand2: Int): Int
}