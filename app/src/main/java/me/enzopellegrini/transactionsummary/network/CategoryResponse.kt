package me.enzopellegrini.transactionsummary.network

import com.squareup.moshi.Json

data class Category(
    val category_id: String,
    val group: String,
    @Json(name="hierarchy") val equivalents: List<String>
) {
    fun topEq(): String =
        if (equivalents.isNotEmpty())
            equivalents.first()
        else
            "Default Category"
}

data class CategoryResponse (
    val categories: List<Category>,
    val request_id: String
)
