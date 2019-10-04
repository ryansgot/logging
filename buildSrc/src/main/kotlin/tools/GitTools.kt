package tools

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object GitTools {
    fun gitHash(isShort: Boolean = true): String {
        val shaLen = if (isShort) "--short=7" else "--short=40"
        return "git rev-parse $shaLen HEAD".runCommand().trim()
    }


    private fun String.runCommand(workingDir: File = File(System.getProperty("user.dir"))): String {
        try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(10, TimeUnit.MINUTES)
            return proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            throw RuntimeException(e)
        }
    }
}