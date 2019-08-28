package org.zeroxlab.momodict.input

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch
import org.zeroxlab.momodict.Controller
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.concurrent.TimeUnit

private const val INPUT_DELAY = 300

class InputPresenter(
    val context: FragmentActivity,
    val view: InputContract.View
) : InputContract.Presenter {
    /**
     * User input won't be send to ctrl directly. Instead, send to here so we have more flexibility
     * to use controller.
     */
    private val query: Subject<String, String>
    private val controller: Controller = Controller(context)

    init {
        // This fragment might be destroy if user scroll to third Tab, so we have to re-create it
        // in onCreate callback.
        query = PublishSubject.create<String>()
        // If user type quickly, do not query until user stop inputting.
        query.debounce(INPUT_DELAY.toLong(), TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ input ->
                context.lifecycle.coroutineScope.launch {
                    val entries = controller.queryEntries(input)
                    view.onUpdateList(entries)
                    view.setLoading(false)
                }
            }) { e -> e.printStackTrace() }
    }

    override fun onResume() {
        // If there is no any available dictionary, disable Input view.
        controller.getBooks(context.lifecycle.coroutineScope) {
            view.onEnableInput(it.isNotEmpty())
            if (it.isNotEmpty()) {
                view.inputSelectAll()
            }
        }
    }

    override fun onDestroy() {
        query.onCompleted()
    }

    override fun changeText(text: String) {
        val input = text.trim { it <= ' ' }
        if (TextUtils.isEmpty(input)) {
            view.onUpdateList(listOf())
        } else {
            view.setLoading(true)
            query.onNext(input)
        }
    }
}
