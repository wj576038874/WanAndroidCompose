package com.wanandroid.compose.bookmark.receiver

import io.github.santimattius.structured.annotations.StructuredScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Owns the supplied per-broadcast scope until all work and its children finish. */
internal fun launchBroadcastTask(
    @StructuredScope scope: CoroutineScope,
    onFinished: () -> Unit,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val job = scope.launch(block = block)
    // Unlike a finally inside launch, this also runs if cancellation prevents the body
    // from starting, and only runs after all child coroutines have completed.
    job.invokeOnCompletion {
        try {
            scope.cancel()
        } finally {
            onFinished()
        }
    }
    return job
}
