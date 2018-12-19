package com.grace.placessearch.common.log

import android.util.Log

import java.util.regex.Matcher
import java.util.regex.Pattern

import timber.log.Timber

/**
 * From the original [Timber.DebugTree].
 */
class DebugTree(protected val applicationTag: String) : Timber.DebugTree() {

    override fun v(message: String?, vararg args: Any) {
        throwShade(Log.VERBOSE, formatString(message, *args), null)
    }

    override fun v(t: Throwable, message: String?, vararg args: Any) {
        throwShade(Log.VERBOSE, formatString(message, *args), t)
    }

    override fun d(message: String?, vararg args: Any) {
        throwShade(Log.DEBUG, formatString(message, *args), null)
    }

    override fun d(t: Throwable, message: String?, vararg args: Any) {
        throwShade(Log.DEBUG, formatString(message, *args), t)
    }

    override fun i(message: String?, vararg args: Any) {
        throwShade(Log.INFO, formatString(message, *args), null)
    }

    override fun i(t: Throwable, message: String?, vararg args: Any) {
        throwShade(Log.INFO, formatString(message, *args), t)
    }

    override fun w(message: String?, vararg args: Any) {
        throwShade(Log.WARN, formatString(message, *args), null)
    }

    override fun w(t: Throwable, message: String?, vararg args: Any) {
        throwShade(Log.WARN, formatString(message, *args), t)
    }

    override fun e(message: String?, vararg args: Any) {
        throwShade(Log.ERROR, formatString(message, *args), null)
    }

    override fun e(t: Throwable, message: String?, vararg args: Any) {
        throwShade(Log.ERROR, formatString(message, *args), t)
    }

    protected fun throwShade(priority: Int, message: String?, t: Throwable?) {
        var message = message
        if (message == null || message.isEmpty()) {
            if (t != null) {
                message = Log.getStackTraceString(t)
            } else {
                // Swallow message if it's null and there's no throwable.
                return
            }
        } else if (t != null) {
            message += "\n" + Log.getStackTraceString(t)
        }

        val tag = createTag()
        if (message!!.length < 4000) {
            /*
             * VV: this is the only place that needs refactoring
             * Replacing the original tag with the application tag and
             * using the tag for the class formatted at the beginning.
             */
            val taggedMessage = String.format("%s -> %s", tag, message)
            Log.println(priority, applicationTag, taggedMessage)
        } else {
            // It's rare that the message will be this large, so we're ok with the perf hit of splitting
            // and calling Log.println N times.  It's possible but unlikely that a single line will be
            // longer than 4000 characters: we're explicitly ignoring this case here.
            val lines = message.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (line in lines) {
                Log.println(priority, tag, line)
            }
        }
    }

    companion object {
        protected val NEXT_TAG = ThreadLocal<String>()
        protected val ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$")

        protected fun createTag(): String {
            var tag: String? = NEXT_TAG.get()
            if (tag != null) {
                NEXT_TAG.remove()
                return tag
            }

            val stackTrace = Throwable().stackTrace

            if (stackTrace.size < 6) {
                throw IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?")
            }

            val stackTraceElement = stackTrace[5]
            tag = stackTraceElement.className
            val m = ANONYMOUS_CLASS.matcher(tag!!)
            if (m.find()) {
                tag = m.replaceAll("")
            }

            tag = String.format("%20s %20s", tag!!.substring(tag.lastIndexOf('.') + 1), stackTraceElement.methodName)
            return tag.substring(tag.lastIndexOf('.') + 1)
        }

        internal fun formatString(message: String?, vararg args: Any): String? {
            // If no varargs are supplied, treat it as a request to log the string without formatting.
            return if (args.size == 0) message else String.format(message!!, *args)
        }
    }
}