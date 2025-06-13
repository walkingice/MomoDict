package cc.jchu.momodict.archive

class Word {
    var entry: IdxEntry? = null
    var data: String? = null

    override fun toString(): String = "$entry\n$data"
}
