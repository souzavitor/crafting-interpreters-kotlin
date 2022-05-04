package com.craftinginterpreters.lox

sealed class Stmt {
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVariableStmt(stmt: Variable): R
        fun visitBlockStmt(stmt: Block): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitExpressionStmt(this);
    }

    class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitPrintStmt(this)
    }

    class Variable(val name: Token, val expression: Expr? = null) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitVariableStmt(this)
    }

    class Block(val statements : List<Stmt?>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitBlockStmt(this)
    }
}


