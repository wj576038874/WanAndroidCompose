package com.wanandroid.compose.opensource

import com.wanandroid.compose.opensource.repository.impl.LocalOpenSourceRepository
import java.net.URI
import org.junit.Assert.*
import org.junit.Test

class OpenSourceRepositoryTest {
    @Test fun `catalog contains the projects used by network image and compose features`() {
        val names = LocalOpenSourceRepository().getLibraries().map { it.name }
        assertTrue(names.containsAll(listOf("Jetpack Compose", "Retrofit", "OkHttp", "Gson", "Coil", "Dagger / Hilt")))
    }

    @Test fun `every item has a unique identity description and GitHub repository URL`() {
        val libraries = LocalOpenSourceRepository().getLibraries()
        assertTrue(libraries.isNotEmpty())
        assertEquals(libraries.size, libraries.map { it.id }.distinct().size)
        libraries.forEach {
            assertTrue(it.name.isNotBlank())
            assertTrue(it.descriptionRes != 0)
            val uri = URI(it.githubUrl)
            assertEquals("https", uri.scheme)
            assertEquals("github.com", uri.host)
            assertTrue(uri.path.split('/').filter(String::isNotBlank).size >= 2)
        }
    }
}
