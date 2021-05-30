package tj.livo.artesia.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import tj.livo.artesia.R
import tj.livo.artesia.models.Order

class OrderHistoryAdapter(
    val context: Fragment, val orderCall: OrderCall
) : RecyclerView.Adapter<OrderHistoryAdapter.ProductHolder>() {

    var products = mutableListOf<Order>()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.order_history_item, null)
        return ProductHolder(view)
    }

    fun setData(items: MutableList<Order>) {
        this.products = items
        this.products.sortByDescending {
            it.id
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]
        var isFull = true // set true if you want to hide detail info & false if you want show it
        holder.orderId.text = "Заказ #" + product.id.toString()
        if (product.delivery_price.toDouble() > 0) {
            holder.delivery.setTextColor(Color.parseColor("#FF0000"))
            holder.delivery.text = "СРОЧНЫЙ"
        } else {
            holder.delivery.setTextColor(Color.parseColor("#FF03DAC5"))
            holder.delivery.text = "ОБЫЧНЫЙ"
        }
        Log.e("STATUS",product.order_status_id.toString())
        if (product.order_status_id == 4){
            holder.addToOrder.visibility = View.GONE
            holder.orderStatusText.visibility = View.VISIBLE
            holder.orderStatusText.text = "ОТМЕНЁН"
            holder.orderStatusText.setTextColor(Color.RED)
        }else if (product.order_status_id ==3){
            holder.addToOrder.visibility = View.GONE
            holder.orderStatusText.visibility = View.VISIBLE
            holder.orderStatusText.text = "ДОСТАВЛЕН"
            holder.orderStatusText.setTextColor(Color.GREEN)
        }else if (product.order_status_id == 2){
            holder.addToOrder.visibility = View.VISIBLE
            holder.orderStatusText.visibility = View.GONE
        }

        holder.reciver.text = product.client.fullname
        holder.balance.text = product.client.balance + " сом"
        holder.clientBottles.text = product.client.bottle.toString() + " шт"
        holder.clientLoanedBottles.text = product.client.loan_bottle.toString() + " шт"
        holder.address.text = product.city + "," + product.street
        holder.deliveryDate.text = product.delivery_time
        holder.comment.text = product.comment
        val adapter = ProductsAdapter(context)
        adapter.setData(product.products.toMutableList())
        holder.recycler_products.layoutManager = GridLayoutManager(context.requireContext(), 1)
        holder.recycler_products.adapter = adapter

        var total = 0.0
        product.products.forEach { product ->
            total += (product.pivot.quantity.toDouble() * product.pivot.price.toDouble())
        }


        holder.discount.text = "СКИДКА : ${product.discount}% | НАЦЕНКА : ${product.markup} сом \n" +
                "ДОСТАВКА : ${product.delivery_price} | ИТОГО : $total сом"

        holder.call.setOnClickListener {
            orderCall.itemClickListener(product.client.phone)
        }
        if (isFull) {
            holder.part1.visibility = View.GONE
            holder.part3.visibility = View.GONE
            isFull = false
        } else {
            holder.part1.visibility = View.VISIBLE
            holder.part3.visibility = View.VISIBLE
            isFull = true
        }

        holder.itemView.setOnClickListener {
            if (isFull) {
                holder.part1.visibility = View.GONE
                holder.part3.visibility = View.GONE
                isFull = false
            } else {
                holder.part1.visibility = View.VISIBLE
                holder.part3.visibility = View.VISIBLE
                isFull = true
            }
        }
        holder.addToOrder.setOnCheckedChangeListener(null)
        holder.addToOrder.isChecked = product.inOrder
        holder.addToOrder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                orderCall.addToOrder(product)
            } else {
                orderCall.removeFromOrder(product)
            }
        }
        if (product.inOrder){
            holder.addToOrder.text = "Уже в очереди"
        }else{
            holder.addToOrder.text = "В очередь"
        }
    }

    inner class ProductHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var orderId: TextView = view.findViewById(R.id.order_id)
        var delivery: TextView = view.findViewById(R.id.delivery)
        var reciver: TextView = view.findViewById(R.id.recipient)
        var balance: TextView = view.findViewById(R.id.balance)
        var clientBottles: TextView = view.findViewById(R.id.bottles)
        var clientLoanedBottles: TextView = view.findViewById(R.id.bottles_loaned)
        var address: TextView = view.findViewById(R.id.address)
        var deliveryDate: TextView = view.findViewById(R.id.delivery_date)
        var comment: TextView = view.findViewById(R.id.comment)
        var recycler_products: RecyclerView = view.findViewById(R.id.recycler_products)
        var discount: TextView = view.findViewById(R.id.discount_and_markup)
        var call: MaterialButton = view.findViewById(R.id.call)
        var part1: ConstraintLayout = view.findViewById(R.id.part1)
        var part2: ConstraintLayout = view.findViewById(R.id.part2)
        val part3: ConstraintLayout = view.findViewById(R.id.part3)
        val addToOrder: Switch = view.findViewById(R.id.add_to_queue)
        val orderStatusText : TextView = view.findViewById(R.id.order_status_id)
    }

    interface OrderCall {
        fun itemClickListener(phone: String)

        fun addToOrder(order: Order)
        fun removeFromOrder(order: Order)
    }
}