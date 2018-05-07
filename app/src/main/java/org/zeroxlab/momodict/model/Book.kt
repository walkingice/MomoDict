package org.zeroxlab.momodict.model

class Book {
    var version: String? = null
    var bookName: String? = null
    var wordCount: Int = 0
    var syncWordCount: Int = 0
    var author: String? = null
    var email: String? = null
    var webSite: String? = null
    var description: String? = null
    var date: String? = null
    var sameTypeSequence: String? = null

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("version: ${this.version}\n")
        sb.append("bookName: ${this.bookName}\n")
        sb.append("wordCount: ${this.wordCount}\n")
        sb.append("syncWordCount: ${this.syncWordCount}\n")
        sb.append("author: ${this.author}\n")
        sb.append("email: ${this.email}\n")
        sb.append("webSite: ${this.webSite}\n")
        sb.append("description: ${this.description}\n")
        sb.append("time: ${this.date}\n")
        sb.append("sameTypeSequence: ${this.sameTypeSequence}\n")
        return sb.toString()
    }
}
