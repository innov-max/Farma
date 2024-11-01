import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val urlToImage: String?
)

suspend fun fetchFarmingNews(): List<Article> {
    val client = HttpClient()

    return try {
        val response: String = client.get("https://https://newsapi.org/v2/everything -G/farming-news").bodyAsText()
        // Example of manual parsing if the response is JSON
        parseArticles(response) // Custom function to parse your articles
    } catch (e: Exception) {
        // Handle error (logging, showing a message, etc.)
        emptyList() // Return an empty list on error
    } finally {
        client.close() // Close the client to prevent memory leaks
    }
}

// Custom function to manually parse JSON string
private fun parseArticles(jsonString: String): List<Article> {
    // Manually parse the JSON string using string manipulation or regex
    // This is a basic example and not recommended for complex JSON
    val articles = mutableListOf<Article>()

    // Simple string manipulation to illustrate the concept (not production-ready)
    val items = jsonString.split(",") // Assuming a simple CSV-like structure for demo purposes
    for (item in items) {
        val parts = item.split(":") // Split title and URL assuming simple JSON format
        if (parts.size == 2) {
            articles.add(Article(parts[0].trim(), parts[1].trim()))
        }
    }

    return articles
}
