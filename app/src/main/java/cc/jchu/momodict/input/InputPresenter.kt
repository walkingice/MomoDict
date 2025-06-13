package cc.jchu.momodict.input

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import cc.jchu.momodict.Controller

private const val INPUT_DELAY = 700L

class InputPresenter(
    val context: FragmentActivity,
    val view: InputContract.View
) : InputContract.Presenter {
    /**
     * User input won't be send to ctrl directly. Instead, send to here so we have more flexibility
     * to use controller.
     */
    private val controller: Controller = Controller(context)
    private var debounceJob: Job? = null

    override fun onResume() {
        context.lifecycle.coroutineScope.launch {
            // If there is no any available dictionary, disable Input view.
            val books = controller.getBooks()
            view.onEnableInput(books.isNotEmpty())
            if (books.isNotEmpty()) {
                view.inputSelectAll()
            }
        }
    }

    override fun changeText(text: String) {
        val input = text.trim { it <= ' ' }
        if (TextUtils.isEmpty(input)) {
            view.onUpdateList(listOf())
        } else {
            view.setLoading(true)
            debounceQuery(input)
        }
    }

    private fun debounceQuery(input: String) {
        debounceJob?.cancel()
        // TODO: can we cancel previous running query as well?
        debounceJob = context.lifecycle.coroutineScope.launch {
            delay(INPUT_DELAY)
            val entries = controller.queryEntries(input)
            view.onUpdateList(entries)
            view.setLoading(false)
        }
    }
}
