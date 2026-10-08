package com.wanandroid.compose.bookmark

import android.content.ActivityNotFoundException
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.wanandroid.compose.R
import com.wanandroid.compose.bookmark.event.BookMarkEvent
import com.wanandroid.compose.bookmark.screen.BookMarkContent
import com.wanandroid.compose.bookmark.viewmodel.BookMarkViewModel
import com.wanandroid.compose.utils.ObserveAsEvents
import com.wanandroid.compose.utils.launchCustomChromeTab
import kotlinx.coroutines.launch

@Composable
fun BookMarkScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<BookMarkViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val resources = LocalResources.current
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()


    ObserveAsEvents(viewModel.events) { event->
        when (event) {
            is BookMarkEvent.Message -> {
                snackbarHostState.showSnackbar(resources.getString(event.resourceId))
            }
            is BookMarkEvent.OpenLink -> {
                try {
                    launchCustomChromeTab(context, event.link.toUri(), toolbarColor)
                } catch (_: ActivityNotFoundException) {
                    snackbarHostState.showSnackbar(resources.getString(R.string.string_bookmark_open_failed))
                }
            }
        }
    }

    BookMarkContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}
