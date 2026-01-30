package cc.jchu.momodict.archive

/**
 * Data structure to present a .ifo file
 */
class Idx {
    val entries = ArrayList<IdxEntry>()

    fun size(): Int {
        return entries.size
    }

    fun get(idx: Int): IdxEntry {
        return entries[idx]
    }
}
