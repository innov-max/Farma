import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?
)
