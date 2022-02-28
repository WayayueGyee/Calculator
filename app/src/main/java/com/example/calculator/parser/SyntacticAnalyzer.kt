package com.example.calculator.parser

import kotlin.math.abs

class UnexpectedLexeme(message: String) : Exception(message)

enum class Token {
    NUMBER,
    OPEN_BRACKET, CLOSING_BRACKET,
    PLUS, MINUS, MUL, DIV,
    EOF
}

class Lexeme {
    val type: Token
    val value: String

    constructor(type: Token, value: String) {
        this.type = type
        this.value = value
    }

    constructor(type: Token, value: Char) {
        this.type = type
        this.value = value.toString()
    }

    override fun toString(): String {
        return "$type: $value"
    }
}

class LexemeBuffer(private val lexemes: MutableList<Lexeme>) {
    private var position: Int = 0

    fun next(): Lexeme {
        return lexemes[position++]
    }

    fun back() {
        position--
    }

    fun getPosition(): Int {
        return position
    }
}

fun lexicalAnalyze(expression: String): MutableList<Lexeme> {
    val lexemes: MutableList<Lexeme> = mutableListOf()
    var position = 0

    while (position < expression.length) {
        var currentChar: Char = expression[position]

        when (currentChar) {
            '(' -> lexemes.add(Lexeme(Token.OPEN_BRACKET, currentChar))
            ')' -> lexemes.add(Lexeme(Token.CLOSING_BRACKET, currentChar))
            '+' -> lexemes.add(Lexeme(Token.PLUS, currentChar))
            '-' -> lexemes.add(Lexeme(Token.MINUS, currentChar))
            '*' -> lexemes.add(Lexeme(Token.MUL, currentChar))
            '/', '÷' -> lexemes.add(Lexeme(Token.DIV, currentChar))
            ' ' -> {}
            else -> {
                if (currentChar in '0'..'9' || currentChar == '.' || currentChar.lowercaseChar() == 'e') {
                    val sb = StringBuilder()

                    do {
                        sb.append(currentChar)
                        position++

                        if (position >= expression.length)
                            break

                        currentChar = expression[position]
                    } while (currentChar in '0'..'9' || currentChar == '.' || currentChar.lowercaseChar() == 'e')

                    // Необходимо отнять, чтобы при переходе на символ, не относящийся к NUMBER,
                    // инкремент в конце цикла не пропустил его
                    position--
                    lexemes.add(Lexeme(Token.NUMBER, sb.toString()))
                } else {
                    throw UnexpectedLexeme(
                        "Unexpected lexeme \"$currentChar\" " +
                                "appeared in expression at position: $position"
                    )
                }
            }
        }

        position++
    }

    lexemes.add(Lexeme(Token.EOF, ""))
    return lexemes
}

const val epsilon = 1e-12

fun calculateResult(expression: String): Number {
    val lexemes = LexemeBuffer(lexicalAnalyze(expression))
    val result: Double = expression(lexemes)

    if (abs(result - result.toLong()) < epsilon)
        return result.toLong()

    return result
}

private fun expression(lexemes: LexemeBuffer): Double {
    val lexeme: Lexeme = lexemes.next()

    if (lexeme.type == Token.EOF) {
        return 0.0
    } else {
        lexemes.back()
    }

    return plusMinus(lexemes)
}

// Если в выражении 2 + 3 - 8 + 6 сохранять -8 как число, то результат будет 5,
// так как подряд будет идти 2 числа: 3 и -8 и в plusMinus сработает блок по-умолчанию
private fun plusMinus(lexemes: LexemeBuffer): Double {
    var result: Double = multiplyDivision(lexemes)

    while (true) {
        val lexeme: Lexeme = lexemes.next()

        when (lexeme.type) {
            Token.PLUS -> result += multiplyDivision(lexemes)
            Token.MINUS -> result -= multiplyDivision(lexemes)
            else -> {
                lexemes.back()
                return result
            }
        }
    }
}

private fun multiplyDivision(lexemes: LexemeBuffer): Double {
    var result: Double = factor(lexemes)

    while (true) {
        var lexeme: Lexeme = lexemes.next()

        when (lexeme.type) {
            Token.MUL -> result *= factor(lexemes)
            Token.DIV -> result /= factor(lexemes)
            Token.OPEN_BRACKET -> {
                result *= expression(lexemes)
                lexeme = lexemes.next()

                if (lexeme.type != Token.CLOSING_BRACKET) {
                    throw UnexpectedLexeme(
                        "Unexpected lexeme \"${lexeme.value}\" " +
                                "appeared in expression at position: ${lexemes.getPosition()}"
                    )
                }
            }
            else -> {
                lexemes.back()
                return result
            }
        }
    }
}

private fun factor(lexemes: LexemeBuffer): Double {
    var lexeme: Lexeme = lexemes.next()
    var result = 1.0

    if (lexeme.type == Token.MINUS) {
        lexeme = lexemes.next()
        result = -1.0
    }

    when (lexeme.type) {
        Token.NUMBER -> result *= lexeme.value.toDouble()
        Token.OPEN_BRACKET -> {
            result *= expression(lexemes)
            lexeme = lexemes.next()

            if (lexeme.type != Token.CLOSING_BRACKET) {
                throw UnexpectedLexeme(
                    "Unexpected lexeme \"${lexeme.value}\" " +
                            "appeared in expression at position: ${lexemes.getPosition()}"
                )
            }
        }
        else -> throw UnexpectedLexeme(
            "Unexpected lexeme \"${lexeme.value}\" " +
                    "appeared in expression at position: ${lexemes.getPosition()}"
        )
    }

    return result
}

//  DONE (1) Сделать умножение на скобку без знака [3(4 - 1) = 9]
//  TODO (2) Сделать проверку на правильную скобочную последовательность
//  DONE (3) Сделать проверку на число в экспоненциальной записи
//  DONE (4) Возвращать INT, когда это возможно

/*fun Double.format(digits:Int) = String.Companion.format(
    java.util.Locale.GERMAN,
    "%#,.${digits}f",
    this
)*/
