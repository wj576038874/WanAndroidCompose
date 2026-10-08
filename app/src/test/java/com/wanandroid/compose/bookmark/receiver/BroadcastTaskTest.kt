package com.wanandroid.compose.bookmark.receiver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class BroadcastTaskTest {
    @Test
    fun `broadcast finishes even when cancelled before the coroutine starts`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        var finished = 0
        var ran = false
        val job = launchBroadcastTask(scope, { finished++ }) { ran = true }
        job.cancel()
        testScheduler.advanceUntilIdle()
        assertFalse(ran)
        assertEquals(1, finished)
        assertFalse(scope.isActive)
    }

    @Test
    fun `broadcast stays alive until child work completes`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        val events = mutableListOf<String>()
        launchBroadcastTask(scope, { events += "finished" }) {
            launch {
                delay(100)
                events += "child"
            }
        }
        testScheduler.advanceUntilIdle()
        assertEquals(listOf("child", "finished"), events)
        assertFalse(scope.isActive)
    }

    @Test
    fun `cancellation during work releases broadcast once`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        var finished = 0
        val job = launchBroadcastTask(scope, { finished++ }) { delay(1_000) }
        testScheduler.runCurrent()
        job.cancel()
        testScheduler.advanceUntilIdle()
        assertEquals(1, finished)
        assertFalse(scope.isActive)
    }
}
