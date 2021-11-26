package me.enzopellegrini.transactionsummary.data

/*
 * This repositories holds the correspondence between category id and category name in a key-value store,
 * or fetches them from the web if they are not in local storage
 */
class CategoriesRepository {
    // TODO: Implement retrofit service and local storage
    fun categoryName(code: String): String = "Default category"
}