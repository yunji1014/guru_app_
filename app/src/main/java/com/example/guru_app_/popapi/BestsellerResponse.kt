package com.example.guru_app_.popapi

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

// BestsellerResponse.kt
@Root(name = "response", strict = false)
data class BestsellerResponse(
    @field:ElementList(name = "item", inline = true)
    var items: MutableList<PopBooks> = mutableListOf()
)

@Root(name = "item", strict = false)
data class PopBooks(
    @field:Element(name = "title")
    var title: String = "",

    @field:Element(name = "author")
    var author: String = "",

    @field:Element(name = "cover")
    var cover: String = ""
)

