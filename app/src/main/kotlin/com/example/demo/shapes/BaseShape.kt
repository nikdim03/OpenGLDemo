package com.example.demo.shapes

import android.content.Context

abstract class BaseShape(protected val context: Context) : IShape {

    @Volatile
    private var initialized: Boolean = false

    final override fun init() {
        if (!initialized) {
            initialize()
            initialized = true
        }
    }

    abstract fun initialize()

    override fun isInitialized(): Boolean {
        return initialized
    }
}