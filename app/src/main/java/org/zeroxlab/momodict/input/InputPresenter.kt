package org.zeroxlab.momodict.input

import android.content.Context
import android.text.TextUtils
import org.zeroxlab.momodict.Controller
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit

private const val INPUT_DELAY = 300

class InputPresenter(
    context: Context,
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
            .subscribeOn(Schedulers.io())
            .concatMap { input -> controller.queryEntries(input).toList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                view.onUpdateList(list)
            }) { e -> e.printStackTrace() }
    }

    override fun onResume() {
        // If there is no any available dictionary, disable Input view.
        controller.books
            .count()
            .subscribe { count ->
                view.onEnableInput(count > 0)
                if (count > 0) {
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
            view.onUpdateList(asList())
        } else {
            query.onNext(input)
        }
    }
}
