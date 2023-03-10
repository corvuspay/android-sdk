package hr.corvuspay.demoshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.helpers.CartSingleton
import hr.corvuspay.demoshop.models.Product
import kotlinx.android.synthetic.main.list_item_shop.view.*
import kotlinx.android.synthetic.main.list_item_cart.view.*
import android.graphics.Paint
import hr.corvuspay.demoshop.events.EmptyListEvent
import hr.corvuspay.demoshop.helpers.Constants.CURRENCY
import hr.corvuspay.demoshop.listeners.ShopProductClickListener
import hr.corvuspay.demoshop.helpers.Format
import hr.corvuspay.demoshop.helpers.ItemViewType
import org.greenrobot.eventbus.EventBus


class ProductAdapter(
    private var productList: ArrayList<Product>,
    private val clickListener: ShopProductClickListener,
    private val itemType: ItemViewType
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (itemType) {
            ItemViewType.TYPE_SHOP -> {
                return ShopProductViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_shop,
                        parent,
                        false
                    )
                )
            }
            ItemViewType.TYPE_CART -> {
                return CartProductViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_cart,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun removeItem(position: Int, productId: Long? = null) {
        productId?.let {
            productList.find { it.id == productId }?.let {
                if (productList.remove(it)) {
                    notifyItemRemoved(position)
                }
            }
        } ?: run {
            notifyItemRemoved(position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShopProductViewHolder -> {
                holder.bindData(productList[position])
            }
            is CartProductViewHolder -> {
                holder.bindData(productList[position])
            }
        }
    }

    inner class ShopProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(product: Product) {
            itemView.apply {
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
                btn_add_to_cart.setOnClickListener {
                    CartSingleton.instance.addProduct(product)
                }
                setOnClickListener {
                    clickListener.productClicked(product)
                }
            }
        }
    }

    inner class CartProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(product: Product) {
            itemView.apply {
                iv_cart_product_image.setImageResource(product.imageResource)
                tv_cart_title.text = product.name
                tv_cart_price.text = Format.formatPrice(product.price, CURRENCY.toString())
                et_cart_quantity.setText("${product.quantity}")
                btn_cart_quantity_decrease.setOnClickListener {
                    if (product.quantity > 1) {
                        CartSingleton.instance.decreaseQuantity(product.id)
                        et_cart_quantity.setText("${product.quantity}")
                    }
                }
                btn_cart_quantity_increase.setOnClickListener {
                    if (product.quantity < 99) {
                        CartSingleton.instance.increaseQuantity(product.id)
                        et_cart_quantity.setText("${product.quantity}")
                    }
                }
                btn_cart_remove_item.setOnClickListener {
                    CartSingleton.instance.removeProduct(product.id, adapterPosition)
                    if (CartSingleton.instance.getQuantity() <= 0){
                        EventBus.getDefault().post(EmptyListEvent(true))
                    }else{
                        EventBus.getDefault().post(EmptyListEvent(false))
                    }
                }
                setOnClickListener {
                    clickListener.productClicked(product)
                }
            }
        }
    }
}