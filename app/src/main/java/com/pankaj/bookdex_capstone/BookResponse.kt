package com.pankaj.bookdex_capstone

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val pageCount: Int?
)


data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)
