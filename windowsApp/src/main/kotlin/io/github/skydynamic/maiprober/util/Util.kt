package io.github.skydynamic.maiprober.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.util.concurrent.locks.LockSupport

@Composable
fun DrawableResource.asIcon(): Painter {
    return painterResource(this)
}

internal fun runCommand(vararg command: String): Process? {
    return ProcessBuilder().command(*command).start()
}

internal fun runCommandForResult(vararg command: String): String {
    val pb = ProcessBuilder()
    pb.command(*command)
        .redirectErrorStream(true)
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
    val proc = pb.start()
    val lines = mutableListOf<String>()
    val job = GlobalScope.launch {
        lines += proc.inputStream.bufferedReader()
            .readLines()
    }
    while (proc.isAlive) LockSupport.parkNanos(1000)
    runBlocking {
        job.join()
    }
    return lines.joinToString("\n")
}