package com.example.hw4.ui

import android.util.Log
import com.example.hw4.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hw4.data.api.ApiClient
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Dp
import com.example.hw4.data.api.ApiService
import com.example.hw4.data.model.ArtistResult

import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    isLoggedIn: Boolean,
    onNavigateToArtist: (artistId: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery  by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ArtistResult>>(emptyList()) }
    var favoriteIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var apiService: ApiService = ApiClient.apiService

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            try {
                val me = apiService.getMyData()
                favoriteIds = me.body()?.data?.favorite?.map { it.artistId }?.toSet() ?: emptySet()
            } catch (e: Exception) {
                favoriteIds = emptySet()
            }
        } else {
            favoriteIds = emptySet()
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            results = try {
                apiService.getSearchData(searchQuery)._embedded.results
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            results = emptyList()
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClose = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(results) { item ->
                val artistId = item._links.self.href.substringAfterLast("/")
                val isFav = favoriteIds.contains(artistId)
                ResultCard(
                    item        = item,
                    isLoggedIn  = isLoggedIn,
                    isFavorite  = isFav,
                    onArtistClick    = { onNavigateToArtist(artistId) },
                    onToggleFavorite = { id, wasFav ->
                        scope.launch {
                            val call = if (wasFav) {
                                apiService.removeArtist(id)
                            }
                            else {
                                apiService.likeArtist  (id)
                            }
                            if (call.isSuccessful) {
                                favoriteIds = if (wasFav)
                                    favoriteIds - id
                                else
                                    favoriteIds + id

                                snackbarHostState.showSnackbar(
                                    if (wasFav) {
                                        "Removed from favorites"
                                    }
                                    else {
                                        "Added to favorites"
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester    = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp,
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp).padding(top = 40.dp, start = 5.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search icon"
            )

            Spacer(Modifier.width(8.dp))

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search artists…") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Clear search"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
            )
        }
    }
}

@Composable
fun ResultCard(
    item: ArtistResult,
    isFavorite: Boolean,
    onArtistClick: (String) -> Unit,
    isLoggedIn: Boolean,
    onToggleFavorite: (artistId: String, currentIsFavorite: Boolean) -> Unit
) {
    val artistId = item._links.self.href.substringAfterLast("/")
    var visualIsFavorite by remember { mutableStateOf(isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .clickable{onArtistClick(artistId)},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            if (item._links.thumbnail.href.endsWith("/assets/shared/missing_image.png")) {
                Image(
                    painter = painterResource(R.drawable.placeholder),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item._links.thumbnail.href)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder),
                    error       = painterResource(R.drawable.placeholder),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            if (isLoggedIn) {
                IconButton(onClick = {
                    onToggleFavorite(artistId, visualIsFavorite)
                    visualIsFavorite = !visualIsFavorite
                }, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .size(36.dp)) {
                    Icon(
                        imageVector = if (visualIsFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
//                        tint = if (visualIsFavorite) Color(0xffffc107) else Color.Black,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = "See details",
                    )
                }
            }
        }
    }
}

