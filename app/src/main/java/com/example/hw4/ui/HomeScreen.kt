package com.example.hw4.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hw4.data.api.ApiClient
import com.example.hw4.data.api.ApiService
import com.example.hw4.data.model.Data
import com.example.hw4.data.model.Favorite
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.time.Duration
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    onNavigateToSearch: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToArtist: (artistId: String) -> Unit,
    onDeleteAccSuccessful: () -> Unit,
    onLogoutSuccessful: () -> Unit
    ) {
    val todayString = remember {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
    }
    val favorites = remember { mutableStateListOf<Favorite>() }
    var userdata by remember { mutableStateOf<Data?>(null) }
    val uriHandler = LocalUriHandler.current
    var apiService: ApiService = ApiClient.apiService
    val scope = rememberCoroutineScope()
    val relativeTimes = remember { mutableStateMapOf<String, String>() }


    fun getRelativeTime(isoTime: String?): String {
        if (isoTime == null) return "just now"

        return try {
            val parsedTime = parseIso(isoTime)?.time ?: return "just now"
            val now = System.currentTimeMillis()
            val diff = now - parsedTime
            if (diff < 0) {
                return "just now"
            }
            when {
                diff <= 61_000 -> "${diff / 1000} second${if (diff / 1000 != 1L) "s" else ""} ago"
                diff < 3_600_000 -> "${diff / 60_000} minute${if (diff / 60_000 != 1L) "s" else ""} ago"
                diff < 86_400_000 -> "${diff / 3_600_000} hour${if (diff / 3_600_000 != 1L) "s" else ""} ago"
                diff < 2_592_000_000 -> "${diff / 86_400_000} day${if (diff / 86_400_000 != 1L) "s" else ""} ago"
                else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(parsedTime))
            }
        } catch (e: Exception) {
            Log.e("TimeUtils", "Error parsing time: $isoTime", e)
            "just now"
        }
    }

    fun updateRelativeTimes() {
        favorites.forEach { favorite ->
            favorite.likedAt?.let { likedAt ->
                relativeTimes[favorite.artistId] = getRelativeTime(likedAt)
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        scope.launch {
            if (isLoggedIn) {
                try {
                    val res = apiService.getMyData()
                    if (res.isSuccessful) {
                        userdata = res.body()?.data
                        val rawFavs = userdata?.favorite ?: emptyList()
                        val sorted = rawFavs
                            .sortedByDescending { parseIso(it.likedAt)?.time ?: 0L }
                        favorites.clear()
                        favorites.addAll(sorted)
                    } else {
                        val errorCode = res.code()
                        val errorBody = res.errorBody()?.string() ?: "No error body"
                        Log.e("HomeScreen", "API call failed: HTTP $errorCode - $errorBody")
                    }
                    updateRelativeTimes()
                    while (true) {
                        updateRelativeTimes()
                        delay(1000)
                    }
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Failed to load user data", e)
                    userdata = null
                    favorites.clear()
                }
            } else {
                userdata = null
                favorites.clear()
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Artist Search",
                        fontSize = 5.em,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search Artists"
                        )
                    }
                    Box(contentAlignment = Alignment.Center) {
                        var menuExpanded by remember { mutableStateOf(false) }

                        IconButton(onClick = {
                            if (isLoggedIn) {
                                menuExpanded = true
                            } else {
                                onNavigateToLogin()
                            }
                        }) {
                            if (isLoggedIn && !userdata?.profileImageUrl.isNullOrBlank()) {
                                val personPainter = rememberVectorPainter(image = Icons.Outlined.Person)
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(userdata?.profileImageUrl).crossfade(true).build(),
                                    placeholder = personPainter,
                                    error = personPainter,
                                    contentDescription = "User Profile Menu",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(32.dp).clip(CircleShape)
                                )
                            } else {
                                Icon(Icons.Outlined.Person, "Login")
                            }
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Log out", color = Color(0xFF1E4C8A))},
                                onClick = {
                                    menuExpanded = false
                                    scope.launch {
                                        try {
                                            val logoutRes = apiService.logout()
                                            if (logoutRes.isSuccessful) {
                                                snackbarHostState.showSnackbar("Logged out successfully")
                                                onLogoutSuccessful()
                                            }

                                        } catch (e: Exception) {
                                            Log.d("Logout", "Logged out failed" + e)
                                        }
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete account", color = Color.Red) },
                                onClick = {
                                    menuExpanded = false
                                    scope.launch {
                                        try {
                                            var delRes = apiService.deleteUser()

                                            if (delRes.isSuccessful) {
                                                snackbarHostState.showSnackbar("Deleted user successfully")
                                                onDeleteAccSuccessful()

                                            }

                                        } catch (e: Exception) {
                                            Log.d("Delete", "Delete user failed" + e)
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = todayString,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Favorites",
                    textAlign = TextAlign.Center,
                    fontSize = 4.em,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                if (!isLoggedIn) {
                    ElevatedButton(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 40.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF1E4C8A),
                            contentColor = Color.White
                        ),
                        onClick = onNavigateToLogin
                    ) {
                        Text("Login in to see favorites")
                    }
                } else {

                    if (favorites.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No favorites", fontWeight = FontWeight.Normal)
                        }
                    } else {
                        Box(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                        ) {
                            FavoritesList(
                                favorites = favorites,
                                relativeTimes = relativeTimes,
                                onArtistClick = { artistId ->
                                    onNavigateToArtist(artistId)
                                },
                            )
                        }
                    }
                }

                Text(
                    text = "Powered by Artsy",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).clickable {
                        uriHandler.openUri("https://www.artsy.net/")
                    }
                )
            }
        }
    }
}

@Composable
fun FavoritesList(favorites: List<Favorite>, relativeTimes: Map<String, String>, onArtistClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(favorites, key = { it.artistId }) { fav ->
            FavoriteItem(
                fav = fav,
                relativeTime = relativeTimes[fav.artistId] ?: "just now",
                onClick = { onArtistClick(fav.artistId) }
            )
        }
    }
}

@Composable
fun FavoriteItem(
    fav: Favorite,
    relativeTime: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fav.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = listOfNotNull(fav.nationality, fav.birthday)
                        .joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = relativeTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            IconButton(
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}

private fun parseIso(iso: String): Date? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            return sdf.parse(iso)
        } catch (_: Exception) {}
    }
    return null
}