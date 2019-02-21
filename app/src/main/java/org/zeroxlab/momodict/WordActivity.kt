package org.zeroxlab.momodict

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.zeroxlab.momodict.ui.FragmentListener
import org.zeroxlab.momodict.ui.WordFragment

class WordActivity : AppCompatActivity(), FragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_one_fragment)
        setFragments()
        initActionBar()
    }

    override fun onNotified(from: androidx.fragment.app.Fragment?, type: FragmentListener.TYPE, payload: Any?) {
        if (type == FragmentListener.TYPE.UPDATE_TITLE) {
            if (payload != null) {
                var str = payload as String
                if (str != null) {
                    updateTitle(str)
                }
            }

        }
    }

    private fun initActionBar() {
        val toolbar = findViewById(R.id.actionbar) as Toolbar
        setSupportActionBar(toolbar)
        updateTitle(intent.getStringExtra(Momodict.EXTRA_DATA_1))
    }

    private fun setFragments() {
        val wordFrg = WordFragment.newInstance(intent.getStringExtra(Momodict.EXTRA_DATA_1))
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, wordFrg)
                .commit()
    }

    private fun updateTitle(text: CharSequence) {
        val actionBar = supportActionBar
        actionBar!!.title = text
    }

    companion object {
        fun createIntent(ctx: Context, text: String): Intent {
            val intent = Intent(ctx, WordActivity::class.java)
            intent.putExtra(Momodict.EXTRA_DATA_1, text)
            return intent
        }
    }
}
