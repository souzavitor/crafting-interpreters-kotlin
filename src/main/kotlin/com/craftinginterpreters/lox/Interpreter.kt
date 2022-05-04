package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.ErrorHandling.runtimeError

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private var environment = Environment()

    override fun visitUnaryExpr(expr: Expr.UnaryExpr): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -(right as Double)
            else -> null
        }
    }

    override fun visitGroupingExpr(expr: Expr.GroupingExpr): Any? {
        return evaluate(expr.expr)
    }

    override fun visitLiteralExpr(expr: Expr.LiteralExpr): Any? =
        expr.value

    override fun visitBinaryExpr(expr: Expr.BinaryExpr): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double - right as Double
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double / right as Double
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double * right as Double
            }
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    return left + right
                } else {
                    null
                }
            }

            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double > right as Double
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double >= right as Double
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double <= right as Double
            }
            TokenType.EQUAL_EQUAL -> {
                isEqual(left, right)
            }
            TokenType.BANG_EQUAL -> {
                !isEqual(left, right)
            }

            else -> {
                null
            }
        }
    }

    override fun visitVariableExpr(expr: Expr.VariableExpr): Any? {
        return environment[expr.name]
    }

    override fun visitAssignExpr(expr: Expr.AssignExpr): Any? {
        val value = evaluate(expr.value)

        environment.assign(
            expr.name,
            value
        )
        return value
    }

    override fun visitTernaryExpr(expr: Expr.TernaryExpr): Any? {
        val first = evaluate(expr.first)
        val second = evaluate(expr.second)
        val third = evaluate(expr.third)

        return if (isTruthy(first)) second else third
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        evaluate(stmt.expression)
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        val expr = evaluate(stmt.expression)
        println(stringify(expr))
    }

    override fun visitVariableStmt(stmt: Stmt.Variable) {
        val expression = stmt.expression
        val value = if (expression != null) evaluate(expression) else null

        environment.define(
            stmt.name,
            value
        )
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        executeBlock(stmt.statements, Environment(environment));
    }

    private fun executeBlock(
        statements: List<Stmt?>,
        environment: Environment?
    ) {
        val previous = this.environment
        try {
            this.environment = environment!!
            for (statement in statements) {
                execute(statement!!)
            }
        } finally {
            this.environment = previous
        }
    }

    fun interpret(statements: List<Stmt?>) {
        try {
            for (statement in statements) {
                if (statement != null) execute(statement)
            }
        } catch (error: RuntimeError) {
            runtimeError(error)
        }
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    private fun evaluate(expr: Expr): Any? =
        expr.accept(this)

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        return if (value is Boolean)
            value
        else true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        return a == b
    }

    private fun checkNumberOperands(
        operator: Token, left: Any?, right: Any?
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun stringify(`object`: Any?): String? {
        if (`object` == null) return "nil"
        if (`object` is Double) {
            var text = `object`.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return `object`.toString()
    }
}
