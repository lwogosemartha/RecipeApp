package com.example.recipeapp.data

import androidx.lifecycle.ViewModel
import com.example.recipeapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

data class Recipe(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ingredients: String = "",
    val directions: String = "",
    val imageResId: Int = R.drawable.ic_default_recipe
)


class RecipeViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("recipes")

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    init {
        fetchRecipesFromFirebase()
    }

    private fun fetchRecipesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipeList = mutableListOf<Recipe>()
                for (child in snapshot.children) {
                    val recipe = child.getValue(Recipe::class.java)
                    if (recipe != null) {
                        recipeList.add(recipe)
                    }
                }
                _recipes.value = recipeList
            }

            override fun onCancelled(error: DatabaseError) {
                // Log or handle error
            }
        })
    }

    fun addRecipe(name: String, description: String, ingredients: String, directions: String ) {
        val newId = database.push().key ?: return


        val recipe = Recipe(
            id = newId,  // Use hash for uniqueness
            name = name,
            description = description,
            ingredients = ingredients,
            directions = directions

        )

        database.child(newId).setValue(recipe)
    }
}
