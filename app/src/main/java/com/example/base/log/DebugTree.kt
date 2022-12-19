package com.example.base.log

import timber.log.Timber

class DebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${element.lineNumber} - ${super.createStackElementTag(element)}"
    }
}