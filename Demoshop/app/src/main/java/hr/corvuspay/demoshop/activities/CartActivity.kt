package hr.corvuspay.demoshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.activities.ProductDetailsActivity.Companion.EXTRA_PRODUCT
import hr.corvuspay.demoshop.adapters.ProductAdapter
import hr.corvuspay.demoshop.base.BaseActivity
import hr.corvuspay.demoshop.events.CartItemQuantityChanged
import hr.corvuspay.demoshop.events.CartItemRemoved
import hr.corvuspay.demoshop.events.EmptyListEvent
import hr.corvuspay.demoshop.helpers.*
import hr.corvuspay.demoshop.helpers.Constants.CURRENCY
import hr.corvuspay.demoshop.listeners.ShopProductClickListener
import hr.corvuspay.demoshop.models.Product
import kotlinx.android.synthetic.main.activity_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : BaseActivity(), ShopProductClickListener {

    private lateinit var productAdapter: ProductAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        supportActionBar?.apply {
            title = "Cart"
            setDisplayHomeAsUpEnabled(true)
        }

        if (cartContainsItems())
            initializeContent()
        else
            hideBottomCheckoutView()
    }

    override fun productClicked(product: Product) {
        startActivity(
            Intent(this, ProductDetailsActivity::class.java)
                .putExtra(EXTRA_PRODUCT, product)
        )
        enterAnimation()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun hideBottomCheckoutView() {
        cl_bottom_content.visibility = View.GONE
        empty_layout.visibility = View.VISIBLE
    }

    private fun initializeContent() {
        empty_layout.visibility = View.GONE
        setupRecyclerView()
        defineButtonActions()
        tv_total.text = calculatePrice()
    }

    private fun defineButtonActions() {
        btn_checkout.setOnClickListener {
            startActivity(Intent(this, PaymentParamsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        productAdapter =
            ProductAdapter(CartSingleton.instance.getProducts(), this, ItemViewType.TYPE_CART)
        rv_cart.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_cart.adapter = productAdapter
    }


    private fun cartContainsItems(): Boolean = CartSingleton.instance.getQuantity() > 0

    private fun calculatePrice() =
        Format.formatPrice(CartSingleton.instance.getDiscountSubtotal(), CURRENCY.toString())

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemRemoved(event: CartItemRemoved) {
        event.position?.let {
            productAdapter.removeItem(it)
        }
        tv_total.text = calculatePrice()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartQuantityChanged(event: CartItemQuantityChanged) {
        tv_total.text = calculatePrice()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEmptyProductList(event: EmptyListEvent) {
        if (event.isEmpty) {
            hideBottomCheckoutView()
        }
    }
}
