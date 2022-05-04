package com.craftinginterpreters.lox

sealed class Expr {
    interface Visitor<R> {
        fun visitUnaryExpr(expr: UnaryExpr): R
        fun visitGroupingExpr(expr: GroupingExpr): R
        fun visitLiteralExpr(expr: LiteralExpr): R
        fun visitBinaryExpr(expr: BinaryExpr): R
        fun visitTernaryExpr(expr: TernaryExpr): R
        fun visitVariableExpr(expr: VariableExpr): R
        fun visitAssignExpr(expr: AssignExpr): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    class UnaryExpr(
        val operator: Token,
        val right: Expr
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitUnaryExpr(this)
    }

    class GroupingExpr(
        val expr: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGroupingExpr(this)
    }

    class LiteralExpr(
        val value: Any? // Number, String, Boolean, nil
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitLiteralExpr(this)
    }

    class BinaryExpr(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBinaryExpr(this)
    }

    class TernaryExpr(
        val first: Expr,
        val second: Expr,
        val third: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitTernaryExpr(this)
    }

    class VariableExpr(val name: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitVariableExpr(this);
    }

    class AssignExpr(val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitAssignExpr(this);
    }
}



