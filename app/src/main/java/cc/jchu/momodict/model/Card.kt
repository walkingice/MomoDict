package cc.jchu.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cards")
data class Card(
        @PrimaryKey
        var wordStr: String
) {
    var time: Date? = null
    var note: String? = null
}
