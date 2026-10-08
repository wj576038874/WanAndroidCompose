package com.wanandroid.compose.bookmark

import com.wanandroid.compose.bookmark.repository.BookMarkRepository
import jakarta.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class BrowserBookMarkSaver @Inject constructor(
    private val repository: BookMarkRepository,
) {
    private val mutex = Mutex()

    suspend fun save(link: String?, title: String? = null, originalLink: String? = null): BrowserBookMarkResult {
        val value = link?.trim().orEmpty()
        val url = value.toHttpUrlOrNull()
        if (url == null || value.any { it.isWhitespace() } ||
            !(value.startsWith("https://", true) || value.startsWith("http://", true))
        ) return BrowserBookMarkResult.INVALID_LINK

        // Includes time waiting for other clicks; finish before goAsync's broadcast deadline.
        return withTimeoutOrNull(SAVE_TIMEOUT_MILLIS.milliseconds) {
            mutex.withLock {
                try {
                    val bookmarks = repository.getBookMarks().getOrThrow()
                    if (bookmarks.any { it.link.toHttpUrlOrNull() == url }) {
                        return@withLock BrowserBookMarkResult.ALREADY_SAVED
                    }
                    val name = title?.trim()?.takeIf {
                        it.isNotEmpty() && originalLink?.toHttpUrlOrNull() == url
                    } ?: url.host
                    repository.addBookMark(name, value).getOrThrow()
                    BrowserBookMarkResult.SAVED
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    BrowserBookMarkResult.FAILED
                }
            }
        } ?: BrowserBookMarkResult.FAILED
    }

    private companion object {
        const val SAVE_TIMEOUT_MILLIS = 8_000L
    }
}

enum class BrowserBookMarkResult { SAVED, ALREADY_SAVED, INVALID_LINK, FAILED }
