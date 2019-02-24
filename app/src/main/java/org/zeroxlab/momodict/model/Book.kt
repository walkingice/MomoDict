package org.zeroxlab.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionaries")
class Book(
        @PrimaryKey
        var bookName: String
) {
    var version: String? = null
    var wordCount: Int = 0
    var syncWordCount: Int = 0
    var author: String? = null
    var email: String? = null
    var webSite: String? = null
    var description: String? = null
    var date: String? = null
    var sameTypeSequence: String? = null

    override fun toString(): String {
        return """
            version: $version
            bookName: $bookName
            wordCount: $wordCount
            syncWordCount: $syncWordCount
            author: $author
            email: $email
            website: $webSite
            description: $description
            time: $date
            sameTypeSequence: $sameTypeSequence
            """
    }
}
