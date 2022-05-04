package com.craftinginterpreters.lox.sandbox

import com.craftinginterpreters.lox.*
import java.io.IOException

class RPNExprVisitor : Expr.Visitor<String> {
    fun print(expr: Expr): String = expr.accept(this)

    override fun visitUnaryExpr(expr: Expr.UnaryExpr): String {
        return expr.right.accept(this) + " " + expr.operator.lexeme
    }

    override fun visitGroupingExpr(expr: Expr.GroupingExpr): String {
        return expr.expr.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.LiteralExpr): String {
        return expr.value.toString()
    }

    override fun visitBinaryExpr(expr: Expr.BinaryExpr): String {
        return expr.left.accept(this) + " " + expr.right.accept(this) + " " + expr.operator.lexeme
    }

    override fun visitTernaryExpr(expr: Expr.TernaryExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpr(expr: Expr.VariableExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitAssignExpr(expr: Expr.AssignExpr): String {
        TODO("Not yet implemented")
    }


}

private object Testing {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val expr = Expr.BinaryExpr(
            Expr.GroupingExpr(
                Expr.BinaryExpr(
                    Expr.LiteralExpr(1),
                    Token(TokenType.PLUS, "+", null, 1),
                    Expr.LiteralExpr(2),
                )
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.GroupingExpr(
                Expr.BinaryExpr(
                    Expr.LiteralExpr(4),
                    Token(TokenType.MINUS, "-", null, 1),
                    Expr.LiteralExpr(2),
                )
            )
        )
        println(RPNExprVisitor().print(expr))
        println(Astrinter().print(expr))
    }
}


