package tj.livo.artesia.adapters
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import tj.livo.artesia.R
import tj.livo.artesia.models.Order

class ActiveOrderAdapter(
    val context: Fragment, val orderCall: OrderCall
) : RecyclerView.Adapter<ActiveOrderAdapter.ProductHolder>() {

    private var products = mutableListOf<Order>()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.active_order_item, null)
        return ProductHolder(view)
    }

    fun setData(items: MutableList<Order>) {
        this.products = items
        notifyDataSetChanged()
    }
   // fun setTimer()

    override fun getItemCount() = products.size

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.orderId.text = "Заказ #" + product.id.toString()

        holder.reciver.text = product.client.fullname
        holder.balance.text = product.client.balance + " сом"
        holder.clientBottles.text = product.client.bottle.toString() + " шт"
        holder.clientLoanedBottles.text = product.client.loan_bottle.toString() + " шт"
        holder.address.text = product.city + "," + product.street
        holder.deliveryDate.text = product.delivery_time
        val adapter = ProductsAdapter(context)
        adapter.setData(product.products.toMutableList())
        holder.recycler_products.layoutManager = GridLayoutManager(context.requireContext(), 1)
        holder.recycler_products.adapter = adapter
        holder.discount.text = "СКИДКА : ${product.discount}% | НАЦЕНКА : ${product.markup} сом \n" +
                "ДОСТАВКА : ${product.delivery_price} | ИТОГО : ${product.total} сом"
        holder.call.setOnClickListener {
            orderCall.itemClickListener(product.client.phone)
        }
        holder.paymentStatus.text = product.payment_id.toString()
        holder.timeRemaining.text = "0 мин"
        holder.cancel.setOnClickListener {
            orderCall.cancel(product)
        }
        holder.toPayment.setOnClickListener {
            val bottlesTaken = holder.bottlesTaken.text.toString().toInt()
            val bottlesLoaned = holder.bottlesLoaned.text.toString().toInt()
            orderCall.toPayment(product,bottlesTaken,bottlesLoaned)
        }
    }

    inner class ProductHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var orderId: TextView = view.findViewById(R.id.order_id)
        var reciver: TextView = view.findViewById(R.id.recipient)
        var balance: TextView = view.findViewById(R.id.balance)
        var clientBottles: TextView = view.findViewById(R.id.bottles)
        var clientLoanedBottles: TextView = view.findViewById(R.id.bottles_loaned)
        var address: TextView = view.findViewById(R.id.address)

        var paymentStatus : TextView = view.findViewById(R.id.payment_status)
        var timeRemaining : TextView = view.findViewById(R.id.time_remaining)

        var deliveryDate: TextView = view.findViewById(R.id.delivery_date)
        var recycler_products: RecyclerView = view.findViewById(R.id.products)
        var discount: TextView = view.findViewById(R.id.discount_and_markup)
        var call: MaterialButton = view.findViewById(R.id.call)
        var cancel : MaterialButton = view.findViewById(R.id.cancel_order)
        var toPayment : MaterialButton = view.findViewById(R.id.to_payment)
        var bottlesTaken : EditText = view.findViewById(R.id.bottles_taken)
        var bottlesLoaned : EditText = view.findViewById(R.id.bottles_loaned)
        }

    interface OrderCall {
        fun itemClickListener(phone: String)
        fun cancel(order: Order)
        fun toPayment(order: Order , bottlesTaken: Int,bottlesLoaned : Int)
    }
}