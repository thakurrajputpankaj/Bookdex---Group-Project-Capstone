package com.pankaj.bookdex_capstone

data class BookFirebase(
    val volumeInfo: VolumeFirebase
)

data class VolumeFirebase(
    val title: String,
    val authors: List<String>,
    val imageLinks: ImageFirebase
)

data class ImageFirebase(
    val thumbnail: String
)
