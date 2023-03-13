package hr.corvuspay.demoshop.base

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hr.corvuspay.demoshop.R
import kotlinx.android.synthetic.main.layout_progress_bar.*

abstract class BaseActivity : AppCompatActivity() {

    fun showProgressBar() {
        layout_progress.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        layout_progress.visibility = View.GONE
    }

    fun enterAnimation() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun exitAnimation() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onBackPressed() {
        finish()
        exitAnimation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}