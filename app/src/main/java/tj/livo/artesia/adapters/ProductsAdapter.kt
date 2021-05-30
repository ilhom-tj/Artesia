package tj.livo.artesia.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.button.MaterialButton
import tj.livo.artesia.R
import tj.livo.artesia.models.Order
import tj.livo.artesia.models.Product

class ProductsAdapter(
    val context: Fragment,
) : RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    private var products = mutableListOf<Product>()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.product_item, null)
        return ProductHolder(view)
    }

    fun setData(items: MutableList<Product>) {
        this.products = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]
        holder.text.text ="- ${product.name} | ${product.pivot.quantity} шт |${(product.pivot.quantity * product.pivot.price.toDouble())} сом"
    }

    inner class ProductHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var text = view.findViewById<TextView>(R.id.text)
    }

}