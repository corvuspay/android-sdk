package hr.corvuspay.demoshop.activities

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.base.BaseActivity
import hr.corvuspay.demoshop.helpers.CartSingleton
import hr.corvuspay.demoshop.helpers.Constants.CURRENCY
import hr.corvuspay.demoshop.helpers.Format
import hr.corvuspay.demoshop.models.Product
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        showProgressBar()
        val product = intent.getSerializableExtra(EXTRA_PRODUCT) as Product

        iv_product_image.setImageResource(product.imageResource)
        tv_title.text = product.name
        tv_price.text = Format.formatPrice(product.price, CURRENCY.toString())
        product.oldPrice?.let {
            tv_old_price.paintFlags = tv_old_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tv_old_price.text = Format.formatPrice(it, CURRENCY.toString())
            tv_old_price.visibility = View.VISIBLE
        } ?: run {
            tv_old_price.visibility = View.GONE
        }
        tv_description.text = product.description
        btn_add_to_cart.setOnClickListener {
            CartSingleton.instance.addProduct(product)
            Toast.makeText(this, "Item added to cart.", Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.title = product.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        hideProgressBar()
    }
}
