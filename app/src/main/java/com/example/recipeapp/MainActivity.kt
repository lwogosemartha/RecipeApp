package com.example.recipeapp


import android.graphics.ColorFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeViewModel
import com.example.recipeapp.ui.theme.RecipeAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        setContent {
            RecipeAppTheme {
                RecipeApp()
            }
        }
    }
}

@Composable
fun RecipeApp() {
    val navController = rememberNavController()
    val viewModel: RecipeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "recipeList"
    ) {
        composable("recipeList") {
            RecipeListScreen(navController = navController, viewModel = viewModel)
        }
        composable("addRecipe") {
            AddRecipeScreen(navController = navController, viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipeViewModel
) {
    val recipeList by viewModel.recipes.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.image_background), // Create this drawable
            contentDescription = null, // Decorative image
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f // Adjust transparency (0.1f - 0.3f recommended)
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.recipe_logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.logo_image_size))
                                    .padding(end = dimensionResource(id = R.dimen.top_bar_logo_padding_end))
                                    .clip(RoundedCornerShape(20)) // Rounded corners
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape // For circular shape, or use RoundedCornerShape
                                    )
                                    .padding(dimensionResource(id = R.dimen.top_bar_logo_inner_padding)),

                                contentScale = ContentScale.Fit
                            )
                            Text(
                                stringResource(R.string.top_bar_title),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.top_bar_text_padding_bottom)) // Small visual alignment
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addRecipe") },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Recipe",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item {
                    Text(
                        stringResource(R.string.welcome_title),
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(
                    items = recipeList,
                    key = { it.id }
                ) { recipe ->
                    RecipeCard(recipe = recipe)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                }
            }
        }
    }
}


@Composable
fun RecipeCard(recipe: Recipe) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.primaryContainer,
        label = "cardBackgroundColor"
    )

    Card(
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.card_horizontal_padding),
                vertical = dimensionResource(id = R.dimen.card_vertical_padding)
            )
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.card_elevation))
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.card_content_padding))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = recipe.imageResId),
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.recipe_image_size))
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_medium)))

                Column(modifier = Modifier.weight(1f)) {
                    Text(recipe.name, style = MaterialTheme.typography.labelMedium)

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

                Text("Ingredients:", style = MaterialTheme.typography.labelMedium)
                recipe.ingredients.split(",").forEach {
                    Text("• ${it.trim()}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

                Text("Directions:", style = MaterialTheme.typography.labelMedium)
                recipe.directions.split(".").forEach {
                    if (it.trim().isNotEmpty()) {
                        Text("• ${it.trim()}.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun RecipeCardPreview() {
    RecipeAppTheme {
        RecipeCard(
            recipe = Recipe(

            )
        )
    }
}