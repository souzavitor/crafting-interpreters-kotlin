package com.craftinginterpreters.lox


class Parser(
    private val tokens: List<Token>
) {
    private var current: Int = 0

    fun parse(): List<Stmt?> {
        val statements = mutableListOf<Stmt?>()
        while (!isAtEnd()) {
            try {
                statements.add(declaration())
            } catch (error: ParseError) {
                synchronize()
                continue;
            }
        }
        return statements;
    }

    private fun declaration(): Stmt? {
        return try {
            if (match(TokenType.VAR)) varDeclaration()
            else statement()
        } catch (error: ParseError) {
            synchronize()
            null
        }
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect the identifier name after the variable name.")

        var expr : Expr? = null
        if (match(TokenType.EQUAL)) {
            expr = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' variable declaration.")
        return Stmt.Variable(name, expr)
    }

    private fun statement(): Stmt {
        if (match(TokenType.PRINT)) return printStmt()
        return if (match(TokenType.LEFT_BRACE)) Stmt.Block(block()) else exprStmt()
    }

    private fun block(): List<Stmt?> {
        val statements: MutableList<Stmt?> = ArrayList()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun printStmt(): Stmt {
        val expression = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(expression)
    };

    private fun exprStmt(): Stmt {
        val expression = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Stmt.Expression(expression)
    };

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = comma()
        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()
            if (expr is Expr.VariableExpr) {
                val name = expr.name
                return Expr.AssignExpr(name, value)
            }

            error(equals, "Invalid assignment target.")
        }
        return expr
    }

    private fun comma(): Expr {
        var expr: Expr = ternary()

        while (match(TokenType.COMMA)) {
            val operator = previous()
            val right: Expr = ternary()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun ternary(): Expr {
        var expr: Expr = logical()

        while (match(TokenType.QUESTION_MARK)) {
            val second = expression()
            consume(TokenType.COLON, "Expect ':' operator for after then branch")
            val third = ternary()
            expr = Expr.TernaryExpr(expr, second, third)
        }

        return expr
    }

    private fun logical(): Expr {
        var expr: Expr = equality()

        while (match(TokenType.AND, TokenType.OR)) {
            val operator = previous()
            val right = equality()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right: Expr = term()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right: Expr = factor()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right: Expr = unary()
            expr = Expr.BinaryExpr(expr, operator, right)
        }

        return expr
    }

    private fun unary(): Expr {
        return if (match(TokenType.BANG, TokenType.MINUS)) {
            Expr.UnaryExpr(previous(), unary())
        } else {
            primary()
        }
    }

    private fun primary(): Expr {
        return when {
            match(TokenType.IDENTIFIER) -> Expr.VariableExpr(previous())
            match(TokenType.TRUE) -> Expr.LiteralExpr(true)
            match(TokenType.FALSE) -> Expr.LiteralExpr(false)
            match(TokenType.NIL) -> Expr.LiteralExpr(null)
            match(TokenType.STRING, TokenType.NUMBER) -> Expr.LiteralExpr(previous().literal)
            match(TokenType.LEFT_PAREN) -> {
                val expr = expression()
                consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
                Expr.GroupingExpr(expr)
            }
            else -> throw error(peek(), "Expect expression.");
        }
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun previous(): Token =
        tokens[current - 1]

    private fun isAtEnd(): Boolean =
        peek().type == TokenType.EOF

    private fun peek(): Token =
        tokens[current]

    private fun check(type: TokenType): Boolean =
        if (isAtEnd()) false else peek().type === type

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type === TokenType.SEMICOLON) return
            when (peek().type) {
                TokenType.CLASS,
                TokenType.FUN,
                TokenType.VAR,
                TokenType.FOR,
                TokenType.IF,
                TokenType.WHILE,
                TokenType.PRINT,
                TokenType.RETURN -> return
                else -> advance()
            }
        }
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        ErrorHandling.error(token, message)
        return ParseError()
    }
}
