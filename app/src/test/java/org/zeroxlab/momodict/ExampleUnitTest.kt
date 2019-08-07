package org.zeroxlab.momodict

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testCoroutine() {
        val myScope = GlobalScope
        whichThread("start")
        val x = myScope.launch {
            whichThread("X 1")
            delay((200))
            val result = withContext(Dispatchers.IO) {
                delay((3300))
                whichThread("X 2")
                9
            }
            whichThread("X 3($result)")
            test()
        }

        val y = myScope.launch {
            whichThread("y 1")
            longTask()
        }

        println("before blocking")

        runBlocking {
            x.join()
            y.join()
        }

        println("done")
    }

    suspend fun test() {
        coroutineScope {
            delay((100))
            println("in coroutineScope")
            delay((100))
        }
    }

    private fun longTask() {
        println("start download")
        Thread.sleep(1000)
        println("finish download")
    }

    private fun whichThread(caller: String?) {
        val who = caller ?: "unknown"
        println("$who, in thread: ${Thread.currentThread()}")
    }

    class Callback(val name: String) {
        fun onSuccess() {
            println("$name, callback success on thread: ${Thread.currentThread()}")
        }

        fun onFailed() {
            println("$name, callback failed on thread: ${Thread.currentThread()}")
        }
    }
}
