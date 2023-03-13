package hr.corvuspay.demoshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.activities.ProductDetailsActivity.Companion.EXTRA_PRODUCT
import hr.corvuspay.demoshop.adapters.ProductAdapter
import hr.corvuspay.demoshop.base.BaseActivity
import hr.corvuspay.demoshop.events.CartItemQuantityChanged
import hr.corvuspay.demoshop.helpers.CartSingleton
import hr.corvuspay.demoshop.helpers.Constants.LOREM_IPSUM
import hr.corvuspay.demoshop.helpers.ItemViewType
import hr.corvuspay.demoshop.listeners.ShopProductClickListener
import hr.corvuspay.demoshop.models.Product
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.layout_badge.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ShopActivity : BaseActivity(),
    ShopProductClickListener {

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        showProgressBar()
        supportActionBar?.setTitle(R.string.app_name)

        val productList = defineProducts()
        productAdapter = ProductAdapter(productList, this, ItemViewType.TYPE_SHOP)
        rv_shop_items.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_shop_items.adapter = productAdapter
        hideProgressBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shop, menu)
        menu.findItem(R.id.action_cart).actionView?.setOnClickListener {
            onOptionsItemSelected(menu.findItem(R.id.action_cart))
        }
        return true
    }


    override fun productClicked(product: Product) {
        startActivity(
            Intent(this, ProductDetailsActivity::class.java)
                .putExtra(EXTRA_PRODUCT, product)
        )
        enterAnimation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                enterAnimation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartQuantityChanged(event: CartItemQuantityChanged) {
        if (event.quantity > 0) {
            tv_badge.text = "${event.quantity}"
            tv_badge.visibility = View.VISIBLE
            Toast.makeText(this, "Item added to cart.", Toast.LENGTH_SHORT).show()
        } else {
            tv_badge.visibility = View.GONE
        }
    }

    private fun defineProducts() =
        arrayListOf(
            Product(
                1,
                "Hoodie with Pocket",
                78.00,
                LOREM_IPSUM,
                1,
                R.drawable.hoodie_with_pocket_768x768,
                99.00
            ),
            Product(2, "Beanie", 29.29, LOREM_IPSUM, 1, R.drawable.beanie_768x768, 35.10),
            Product(3, "Belt", 25.50, LOREM_IPSUM, 1, R.drawable.belt_768x768),
            Product(4, "Hoodie", 154.00, LOREM_IPSUM, 1, R.drawable.hoodie_768x768),
            Product(
                5,
                "Hoodie with Logo",
                179.00,
                LOREM_IPSUM,
                1,
                R.drawable.hoodie_with_logo,
                203.00
            ),
            Product(6, "Polo", 43.00, LOREM_IPSUM, 1, R.drawable.polo_768x767, 55.00),
            Product(7, "Sunglasses", 62.00, LOREM_IPSUM, 1, R.drawable.sunglasses_768x768),
            Product(8, "T-Shirt", 35.00, LOREM_IPSUM, 1, R.drawable.tshirt_768x768),
            Product(9, "V-Neck T-Shirt", 39.00, LOREM_IPSUM, 1, R.drawable.vneck_tee_768x767),
            Product(
                10,
                "Long Sleeve Tee",
                89.00,
                LOREM_IPSUM,
                1,
                R.drawable.long_sleeve_tee_768x768
            ),
            Product(
                11,
                "Hoodie with Zipper",
                200.00,
                LOREM_IPSUM,
                1,
                R.drawable.hoodie_with_zipper_768x768
            )
        )

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        tv_badge?.let {
            if (CartSingleton.instance.getQuantity() > 0) {
                it.text = "${CartSingleton.instance.getQuantity()}"
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
