package com.wanandroid.compose.opensource.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.R
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.opensource.model.OpenSourceLibrary
import com.wanandroid.compose.opensource.viewmodel.OpenSourceViewModel

@Composable
fun OpenSourceScreen(onBackClick: () -> Unit, onOpenLibrary: (OpenSourceLibrary) -> Unit) {
    val viewModel = hiltViewModel<OpenSourceViewModel>()
    val libraries by viewModel.libraries.collectAsStateWithLifecycle()
    OpenSourceContent(libraries, onBackClick, onOpenLibrary)
}

@Composable
internal fun OpenSourceContent(
    libraries: List<OpenSourceLibrary>,
    onBackClick: () -> Unit,
    onOpenLibrary: (OpenSourceLibrary) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { CommonToolbar(title = stringResource(R.string.string_code), onBackClick = onBackClick) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            item(key = "introduction") {
                Text(
                    text = stringResource(R.string.string_oss_intro),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(libraries, key = { it.id }, contentType = { "library" }) { library ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.string_oss_open, library.name),
                            onClick = { onOpenLibrary(library) },
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(library.name, style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text(stringResource(library.descriptionRes), style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
