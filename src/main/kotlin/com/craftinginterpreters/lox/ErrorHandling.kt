package com.craftinginterpreters.lox

object ErrorHandling {
    fun runtimeError(error: RuntimeError) {
        System.err.println(
            error.message.toString() +
                    "\n[line " + error.token.line + "]"
        )
        Lox.hadRuntimeError = true
    }

    fun error(token: Token, message: String?) {
        if (token.type === TokenType.EOF) {
            report(token.line, " at end", message!!)
        } else {
            report(token.line, " at '" + token.lexeme + "'", message!!)
        }
    }

    fun error(line: Int, message: String?) {
        report(line, " at end", message!!)
    }

    private fun report(
        line: Int, where: String,
        message: String
    ) {
        System.err.println(
            "[line $line] Error$where: $message\n"
        )
        Lox.hadError = true
    }
}
