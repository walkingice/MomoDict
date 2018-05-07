package org.zeroxlab.momodict.archive

import org.zeroxlab.momodict.archive.FileSet.Type.DICT
import org.zeroxlab.momodict.archive.FileSet.Type.IDX
import org.zeroxlab.momodict.archive.FileSet.Type.IFO
import rx.functions.Action0

/**
 * A structure for extracted files path.
 */
class FileSet {

    private val mPaths = HashMap<Type, String>()
    private var mCleanCallback: Action0? = null

    /**
     * For a dictionary, to check every necessary file exists.
     * @return true if necessary files exist.
     */
    val isSane: Boolean
        get() = has(IFO) && has(IDX) && has(DICT)

    enum class Type {
        IFO,
        IDX,
        DICT
    }

    fun has(type: Type): Boolean {
        return mPaths.containsKey(type)
    }

    operator fun set(type: Type, path: String) {
        mPaths[type] = path
    }

    operator fun get(type: Type): String? {
        return mPaths[type]
    }

    fun setCleanCallback(cb: Action0) {
        mCleanCallback = cb
    }

    /**
     * Call this to clean extracted files.
     */
    fun clean() {
        if (mCleanCallback != null) {
            mCleanCallback!!.call()
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (t in Type.values()) {
            sb.append(String.format("Type: %d, path: ", t, mPaths[t]))
        }
        return sb.toString()
    }
}
