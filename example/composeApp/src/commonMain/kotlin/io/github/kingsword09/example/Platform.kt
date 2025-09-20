package io.github.kingsword09.example

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform