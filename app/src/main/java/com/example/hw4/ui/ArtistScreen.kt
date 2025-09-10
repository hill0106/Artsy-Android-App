package com.example.hw4.ui

import android.util.Log
import androidx.collection.emptyObjectList
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hw4.data.api.ApiClient
import com.example.hw4.data.api.ApiService
import com.example.hw4.data.model.Artist
import com.example.hw4.data.model.Artwork
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold // Use M3 Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.hw4.R
import com.example.hw4.data.model.GeneX
import com.example.hw4.data.model.SimilarArtistItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    artistId: String,
    isLoggedIn: Boolean,
    onBack: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
) {
    var apiService: ApiService = ApiClient.apiService

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    val favoriteIds = remember { mutableStateOf<Set<String>>(emptySet()) }
    var loading by remember { mutableStateOf(true) }
    var detail by remember { mutableStateOf(Artist()) }
    var artworks by remember { mutableStateOf<List<Artwork>>(emptyList()) }
    var similarArtists    by remember       { mutableStateOf<List<SimilarArtistItem>>(emptyList()) }

    var tabIndex by remember { mutableStateOf(0) }

    var geneId by rememberSaveable { mutableStateOf<String>("") }
    var categories by remember { mutableStateOf<List<GeneX>>(emptyList()) }
    var loadingCategories by remember { mutableStateOf(false) }


    LaunchedEffect(artistId, isLoggedIn) {
        loading = true
        try {
            val response = apiService.getArtist(artistId)
            detail = response
            if (isLoggedIn) {
                val resp = apiService.getMyData()
                if (resp.isSuccessful) {
                    val favs = resp.body()?.data?.favorite ?: emptyList()
                    isFavorite = favs.any { it.artistId == artistId }
                    favoriteIds.value = resp.body()!!.data.favorite.map { it.artistId }.toSet()
//                    Log.d("Artist Fav", isFavorite.toString())
                }
                similarArtists = apiService.getSimilarArtists(artistId)
            }
            artworks =  apiService.getArtwork(artistId)._embedded.artworks
            loading = false
        } catch (e: Exception) {
            Log.e("ArtistScreen", "failed to load artist $artistId", e)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ArtistBar(
                isLoggedIn = isLoggedIn,
                item = detail,
                onBack = onBack,
                isFavorite = isFavorite,
                onToggleFavorite = {
                    scope.launch {
                        try {
                            val response = if (isFavorite) {
                                apiService.removeArtist(artistId)
                            } else {
                                apiService.likeArtist(artistId)
                            }
                            if (response.isSuccessful) {
                                isFavorite = !isFavorite
                                snackbarHostState.showSnackbar(
                                    if (isFavorite) "Added to favorites" else "Removed from favorites"
                                )
                            } else {
                                snackbarHostState.showSnackbar("Failed: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error: ${e.localizedMessage}")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
            Column(Modifier.padding(innerPadding), ) {
                val tabs = listOf("Details", "Artworks") + if (isLoggedIn) listOf("Similar") else emptyList()
                TabRow(selectedTabIndex = tabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            text = { Text(title) },
                            icon = {
                                val icon = when (title) {
                                    "Details"  -> Icons.Outlined.Info
                                    "Artworks" -> Icons.Outlined.AccountBox
                                    else       -> Icons.Filled.PersonSearch
                                }
                                Icon(icon, contentDescription = title)
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (loading) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        when (tabIndex) {
                            0 -> {
                                // DETAILS
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    detail.let { d ->
                                        item {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    d.name,
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                val lifespan = listOfNotNull(
                                                    d.birthday.takeIf { it.isNotBlank() },
                                                    d.deathday.takeIf { it.isNotBlank() }
                                                ).joinToString(" - ")
                                                    .takeIf { it.isNotEmpty() }

                                                val subtitle = listOfNotNull(
                                                    d.nationality.takeIf { it.isNotBlank() },
                                                    lifespan
                                                ).joinToString(", ")
                                                if (subtitle.isNotBlank()) {
                                                    Text(
                                                        text = subtitle,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        textAlign = TextAlign.Center,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                val formattedBio =
                                                    formatBiographyForCompose(d.biography)
                                                if (formattedBio.isNotEmpty()) {
                                                    Text(
                                                        text = formattedBio,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        textAlign = TextAlign.Start
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            1 -> {
                                // ARTWORKS
                                if (artworks.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .background(color = MaterialTheme.colorScheme.primaryContainer,shape = RoundedCornerShape(16.dp))
                                            .height(50.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No Artworks", fontWeight = FontWeight.Normal)
                                    }
                                } else {
                                    LazyColumn(Modifier.fillMaxSize()) {
                                        items(artworks) { art ->
                                            Card(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(15.dp)
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    AsyncImage(
                                                        model = art._links.thumbnail.href,
                                                        contentDescription = art.title,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(360.dp),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(
                                                        art.title,
                                                        Modifier.padding(horizontal = 8.dp),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Button(
                                                        onClick = {
                                                            loadingCategories = true
                                                            categories = emptyList()
                                                            geneId = art.id
                                                            scope.launch {
                                                                try {
                                                                    categories = apiService.getGene(geneId)._embedded.genes
                                                                    Log.d("ArtistScreen", "Categories fetched: ${categories.size}")
                                                                } catch (e: Exception) {
                                                                    Log.e("ArtistScreen", "Failed to fetch categories for $geneId", e)
                                                                    scope.launch { snackbarHostState.showSnackbar("Error loading categories.") }
                                                                    categories = emptyList()
                                                                } finally {
                                                                    loadingCategories = false
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                    ) {
                                                        Text("View categories")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Category Dialog
                                if (geneId != "") {
                                    AlertDialog(
                                        onDismissRequest = {
                                            geneId = ""
                                            loadingCategories = false
                                            categories = emptyList()
                                        },
                                        confirmButton = {
                                            ElevatedButton(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.elevatedButtonColors(
                                                    containerColor = Color(0xFF1E4C8A),
                                                    contentColor   = Color.White
                                                ),
                                                onClick = {
                                                    geneId = ""
                                                    loadingCategories = false
                                                    categories = emptyList()
                                                }
                                            ) {
                                                Text("Close")
                                            }
                                        },
                                        title = { Text("Categories") },
                                        text = {
                                            if (loadingCategories) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                                                    contentAlignment = Alignment.TopCenter
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        CircularProgressIndicator()
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            text = "Loading...",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            } else if (categories.isEmpty()) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(60.dp)
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "No categories available",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            } else {
                                                val pagerState = rememberPagerState{categories.size}
                                                Box(modifier = Modifier
                                                    .fillMaxWidth()
                                                    ) {
                                                    HorizontalPager(
                                                        contentPadding = PaddingValues(horizontal = 30.dp),
                                                        state = pagerState,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(450.dp)
                                                    ) { page ->
                                                        val cat = categories[page]
                                                        Card(
                                                            modifier = Modifier.width(380.dp)
                                                                .fillMaxHeight()
                                                                .padding(horizontal = 3.dp),
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                AsyncImage(
                                                                    model = cat._links.thumbnail.href,
                                                                    contentDescription = cat.name,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(150.dp),
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                                Spacer(Modifier.height(8.dp))
                                                                Text(cat.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                                                Spacer(Modifier.height(4.dp))
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .fillMaxWidth()
                                                                        .padding(horizontal = 16.dp).padding(bottom = 10.dp)
                                                                        .verticalScroll(rememberScrollState())
                                                                ) {
                                                                    Text(
                                                                        text = cat.description
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth().align(Alignment.Center).width(450.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                scope.launch {
                                                                    val prevPage = (pagerState.currentPage - 1).coerceAtLeast(0)
                                                                    pagerState.animateScrollToPage(prevPage)
                                                                }
                                                            },
                                                            enabled = pagerState.currentPage > 0
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                                                contentDescription = "Previous Category"
                                                            )
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                scope.launch {
                                                                    val nextPage = (pagerState.currentPage + 1).coerceAtMost(categories.size - 1)
                                                                    pagerState.animateScrollToPage(nextPage)
                                                                }
                                                            },
                                                            enabled = pagerState.currentPage < categories.size - 1
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                                contentDescription = "Next Category"
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            2 -> {
                                // SIMILAR
                                if (similarArtists.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .background(color = MaterialTheme.colorScheme.primaryContainer,shape = RoundedCornerShape(16.dp))
                                            .height(50.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No Similar Artists", fontWeight = FontWeight.Normal)
                                    }
                                } else {
                                    SimilarArtistsList(
                                        similar = similarArtists,
                                        isLoggedIn = isLoggedIn,
                                        favoriteIds = favoriteIds.value,
                                        onArtistClick = { id -> onNavigateToArtist(id) },
                                        onToggleFavorite = { id, nowFav ->
                                            scope.launch {
                                                val call = if (nowFav) apiService.likeArtist(id)
                                                else            apiService.removeArtist(id)
                                                if (call.isSuccessful) {
                                                    favoriteIds.value =
                                                        if (nowFav) favoriteIds.value + id
                                                        else        favoriteIds.value - id
                                                    snackbarHostState.showSnackbar(
                                                        if (nowFav) "Added to favorites" else "Removed from favorites"
                                                    )
                                                } else {
                                                    snackbarHostState.showSnackbar("Error: ${call.code()}")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun SimilarArtistsList(
    similar: List<SimilarArtistItem>,
    isLoggedIn: Boolean,
    favoriteIds: Set<String>,
    onArtistClick: (String) -> Unit,
    onToggleFavorite: (artistId: String, currentlyFav: Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(similar, key = { it.artistId }) { item ->
            var isFav by rememberSaveable { mutableStateOf(item.artistId in favoriteIds) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable{onArtistClick(item.artistId)},
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box {
                    if (item.image.endsWith("missing_image.png")) {
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
                                .data(item.image)
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
                        IconButton(
                            onClick = {
                            isFav = !isFav
                            onToggleFavorite(item.artistId, isFav)
                        }, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(color = MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .size(36.dp)) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
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
    }
}


@Composable
fun ArtistBar(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    item: Artist,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp).padding(top = 40.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "back"
                )
            }

            Text(text = item.name, fontSize = 5.em, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            if (isLoggedIn) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
//                        tint = if (isFavorite) Color(0xffffc107) else Color.Black,
                    )
                }
            }
        }
    }
}

fun formatBiographyForCompose(biography: String?): String {
    if (biography.isNullOrBlank()) {
        return ""
    }

    return biography
        .replace("- ", "")
}