package com.craftinginterpreters.lox.sandbox

import com.craftinginterpreters.lox.*


class Astrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visitUnaryExpr(expr: Expr.UnaryExpr): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.GroupingExpr): String {
        return parenthesize("group", expr.expr)
    }

    override fun visitLiteralExpr(expr: Expr.LiteralExpr): String {
        if (expr.value == null) return "nil";
        return expr.value.toString()
    }

    override fun visitBinaryExpr(expr: Expr.BinaryExpr): String {
        return parenthesize(
            expr.operator.lexeme,
            expr.left,
            expr.right
        )
    }

    private fun parenthesize(name: String, vararg exprs: Expr?): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr?.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    private fun parenthesize(vararg exprs: String?): String {
        val builder = StringBuilder()
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr)
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitTernaryExpr(expr: Expr.TernaryExpr): String {
        return parenthesize(
            "ternary",
            expr.first,
            expr.second,
            expr.third
        )
    }

    override fun visitVariableExpr(expr: Expr.VariableExpr): String {
        return parenthesize("variable", expr.name.lexeme)
    }

    override fun visitAssignExpr(expr: Expr.AssignExpr): String {
        TODO("Not yet implemented")
    }
}
