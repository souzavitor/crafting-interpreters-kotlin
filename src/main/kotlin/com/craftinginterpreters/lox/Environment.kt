package com.craftinginterpreters.lox

class Environment(
    private val enclosing: Environment? = null
) {
    private val values: MutableMap<String, Any?> = mutableMapOf()

    fun define(name: Token, value: Any?) {
        values[name.lexeme] = value;
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }
        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }
        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }

    operator fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }
        if (enclosing != null) return enclosing[name];

        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }
}
