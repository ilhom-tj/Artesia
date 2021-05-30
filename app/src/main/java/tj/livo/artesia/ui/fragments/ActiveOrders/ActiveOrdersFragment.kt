package tj.livo.artesia.ui.fragments.ActiveOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.DialogInterface.OnShowListener
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.active_order_item.*
import kotlinx.android.synthetic.main.change_product_qty.*
import kotlinx.android.synthetic.main.order_payment_layout.*
import tj.livo.artesia.R
import tj.livo.artesia.Utils
import tj.livo.artesia.adapters.ProductsInActiveAdapter
import tj.livo.artesia.databinding.ActiveOrdersFragmentBinding
import tj.livo.artesia.models.Client
import tj.livo.artesia.models.Order
import tj.livo.artesia.models.Product
import tj.livo.artesia.services.TimerService
import tj.livo.artesia.services.shitCode.TimeService
import tj.livo.artesia.ui.fragments.OrdersHistory.OrdersHistoryViewModel


class ActiveOrdersFragment : Fragment(), ProductsInActiveAdapter.ProductClick {

    private lateinit var reciver: BroadcastReceiver
    private lateinit var viewModel: OrdersHistoryViewModel
    private lateinit var holder: ActiveOrdersFragmentBinding
    private lateinit var order: Order

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("dsa","dsa")
        holder = DataBindingUtil.inflate(inflater, R.layout.active_orders_fragment, container, false)
        holder.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )
            .get(OrdersHistoryViewModel::class.java)
        viewModel.getInProccesOrder().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.e("IDS","sds")
                setupOrder(it[0])
            } else {
                holder.activeLayout.visibility = View.GONE
            }
        })
        return holder.getRoot()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reciver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val remainingTime = intent!!.getIntExtra("remaining_time", 0)
                Log.e("TIME", remainingTime.toString())
                holder.timeRemaining.text = Utils.formatTime(remainingTime)
            }

        }
        val orderStatusFilter = IntentFilter(TimerService.ACTION_SEND_STATUS)
        orderStatusFilter.addCategory(Intent.CATEGORY_DEFAULT)
        requireActivity().registerReceiver(reciver, orderStatusFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(reciver)
    }


    @SuppressLint("SetTextI18n")
    private fun setupOrder(product: Order) {
        order = product
        holder.activeLayout.visibility = View.VISIBLE
        holder.orderId.text = "Заказ #" + product.id.toString()
        holder.recipient.text = product.client.fullname
        holder.balance.text = product.client.balance + " сом"
        holder.bottles.text = product.client.bottle.toString() + " шт"
        holder.bottlesInLoanText.text = product.client.loan_bottle.toString() + " шт"
        holder.address.text = product.city + "," + product.street
        holder.deliveryDate.text = product.delivery_time
        val adapter = ProductsInActiveAdapter(this, this)
        if (product.delivery_price.toDouble() > 0) {
            holder.delivery.setTextColor(Color.parseColor("#FF0000"))
            holder.delivery.text = "СРОЧНЫЙ"
        } else {
            holder.delivery.setTextColor(Color.parseColor("#FF03DAC5"))
            holder.delivery.text = "ОБЫЧНЫЙ"
        }
        adapter.setData(product.products.toMutableList())
        Log.e("PAYMENT", product.payment_id.toString())

        when (product.payment_id) {
            "0" -> {
                holder.paymentStatus.text = "Требуется оплата"
            }
            "1" -> {
                holder.paymentStatus.text = "Оплачено"
            }
            else -> {
                holder.paymentStatus.text = "Другое"
            }
        }
        if (product.payment_id.toString().equals("null")) {
            holder.paymentStatus.text = "Требуется оплата"
        }
        holder.products.layoutManager = GridLayoutManager(requireContext(), 1)
        holder.products.adapter = adapter
        var total = 0.0
        order.products.forEach { product ->
            total += product.pivot.quantity * product.pivot.price.toDouble()
        }
        holder.discountAndMarkup.text = "СКИДКА : ${product.discount}% | НАЦЕНКА : ${product.markup} сом \n" +
                "ДОСТАВКА : ${product.delivery_price} | ИТОГО : $total сом"
        holder.call.setOnClickListener {
            Utils.callTo(this, product.client.phone)
        }
        holder.cancelOrder.setOnClickListener {
            cancelOrder(product)
        }
        holder.toPayment.setOnClickListener {
            Log.e(
                holder.bottlesTaken.text.isNullOrBlank().toString(),
                holder.bottlesLoaned.text.isNullOrEmpty().toString()
            )
            if (!holder.bottlesTaken.text.isNullOrBlank() && !holder.bottlesLoaned.text.isNullOrEmpty()) {
                val bottlesTaken = holder.bottlesTaken.text.toString().toInt()
                val bottlesLoaned = holder.bottlesLoaned.text.toString().toInt()
                finishOrder(product, bottlesTaken, bottlesLoaned, total)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun finishOrder(order: Order, bottlesTaken: Int, bottlesLoaned: Int, total: Double) {
        val paymentView = LayoutInflater.from(context).inflate(R.layout.order_payment_layout, null);
        val alert: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(paymentView)
            .setPositiveButton(getString(R.string.submit_payment), null)
            .setNegativeButton(getString(R.string.cancel_payment), null)
            .create()
        alert.setOnShowListener {
            paymentView.findViewById<TextView>(R.id.subtotal).text = "$total сом"
            val forBottles = paymentView.findViewById<TextView>(R.id.for_bottles)
            val totalText = paymentView.findViewById<TextView>(R.id.total)

            var bottleInOrder = 0
            var waterInOrder = 0

            order.products.forEach {
                if (it.id == 10) {
                    bottleInOrder = it.pivot.quantity
                }
                if (it.id == 1) {
                    waterInOrder = it.pivot.quantity
                }
            }

            var Ttotal = total
            if (isAbleToTakeBottle(order.client, bottlesTaken + bottlesLoaned)) {
                if (isTakenBottleMoreThanOrderBottle(bottlesTaken + bottlesLoaned, bottleInOrder, waterInOrder)) {
                    var forBottlePrice = Math.abs((bottlesTaken - waterInOrder) * 45)
                    forBottles.text = forBottlePrice.toString()
                    Ttotal += forBottlePrice
                } else {
                    if (bottleInOrder > 0 && bottlesLoaned < bottleInOrder && bottlesLoaned > 0) {
                        Log.e(bottleInOrder.toString(), bottlesLoaned.toString())
                        Ttotal -= (bottleInOrder - bottlesLoaned) * 45
                    } else if (bottleInOrder > 0 && bottlesLoaned == bottleInOrder && bottlesLoaned > 0) {
                        Ttotal -= bottleInOrder * 45
                    }
                    forBottles.text = "0"
                }
            } else {
                Toast.makeText(requireContext(), "У клиента меньше бутылок чем взято", Toast.LENGTH_LONG).show()
                alert.dismiss()
            }
            Ttotal += order.delivery_price.toDouble()
            Ttotal += order.markup.toDouble()
            var forDiscount = (Ttotal * order.discount.toDouble()) / 100
            Log.e("FORDis", forDiscount.toString())
            Ttotal -= forDiscount
            totalText.text = "$Ttotal сом"
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val progressDialog = ProgressDialog(requireContext())
                progressDialog.show()
                viewModel.finishOrder(order, bottlesTaken, bottlesLoaned, total, Ttotal).observe(viewLifecycleOwner) {
                    if (it) {
                        Toast.makeText(requireContext(), "Заказ выполнен", Toast.LENGTH_LONG).show()
                        order.isActive = false
                        order.inProcces = false
                        order.order_status_id = 3
                        order.isCompleated = true
                        order.inOrder = false
                        viewModel.setDisActiveOrder(order)
                        viewModel.getInProccesOrder().observe(viewLifecycleOwner) {
                            if (it.isEmpty()) {
                                holder.activeLayout.visibility = View.GONE
                                val timer2 = Intent(requireActivity(), TimeService::class.java)
                                requireActivity().stopService(timer2)
                            } else {
                                Log.e("ID", it[0].id.toString())
                                setupOrder(it[0])
                                val timer2 = Intent(requireActivity(), TimeService::class.java)
                                requireActivity().startService(timer2)
                            }
                        }
                        progressDialog.dismiss()
                        alert.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        alert.dismiss()
                    }

                }
            }
        }
        alert.show()
    }

    private fun isAbleToTakeBottle(client: Client, bottles: Int): Boolean {
        if (client.bottle + client.loan_bottle >= bottles) {
            return true
        }

        return false
    }

    private fun isTakenBottleMoreThanOrderBottle(bottles: Int, bottleInOrder: Int, waterInOrder: Int): Boolean {
        if (bottles + bottleInOrder < waterInOrder) {
            return true
        }
        return false
    }

    private fun cancelOrder(order: Order) {
        val cancelView: View = LayoutInflater.from(context).inflate(R.layout.order_cancel_layout, null)
        val alert: AlertDialog = AlertDialog.Builder(context)
            .setView(cancelView)
            .setPositiveButton(getString(R.string.submit_payment), null)
            .setNegativeButton(getString(R.string.cancel_payment), null)
            .create()
        alert.setOnShowListener(OnShowListener { // set listener for ok button
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
                val cancelReason = (cancelView.findViewById<View>(R.id.cancel_reason) as EditText).text.toString()
                if (cancelReason.isNotEmpty()) {
                    // send details to the server
                    viewModel.cancelOrder(order, cancelReason).observe(viewLifecycleOwner, Observer {
                        it.let {
                            Toast.makeText(requireContext(), "Заказ отменен", Toast.LENGTH_LONG).show()
                            if (it) {
                                val timer = Intent(requireActivity(), TimeService::class.java)
                                requireActivity().stopService(timer)
                                order.isActive = false
                                order.inProcces = false
                                order.order_status_id = 4
                                order.isCompleated = true
                                order.inOrder = false
                                viewModel.setDisActiveOrder(order)
                                viewModel.getInProccesOrder().observe(viewLifecycleOwner) {
                                    if (it.isEmpty()) {
                                        holder.activeLayout.visibility = View.GONE
                                    } else {
                                        Log.e("ID", it[0].id.toString())
                                        setupOrder(it[0])
                                        val timer2 = Intent(requireActivity(), TimeService::class.java)
                                        requireActivity().startService(timer2)
                                    }
                                }
                            }
                        }
                    })
                    alert.dismiss()
                } else {
                    cancelView.findViewById<View>(R.id.empty_cancel_reason).visibility = View.VISIBLE
                }
            })
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(View.OnClickListener { alert.dismiss() })
        })

        alert.show()
    }


    override fun onResume() {
        super.onResume()
        viewModel.getInProccesOrder().observe(viewLifecycleOwner){
            if (!it.isEmpty()){
                setupOrder(it[0])
            }
        }
//        viewModel.getInProccesOrder().observe(viewLifecycleOwner, Observer { it ->
//            if (it.isEmpty()) {
//                viewModel.getActiveOrders().observe(viewLifecycleOwner, Observer {
//                    if (it.isNotEmpty()) {
//                        it[0].inProcces = true
//                        viewModel.setActiveOrder(it[0])
//                        setupOrder(it[0])
//                    }
//                })
//            } else {
//                setupOrder(it[0])
//            }
//        })
    }

    override fun editProductQty(list: List<Product>, position: Int) {
        val qtyView = LayoutInflater.from(requireContext()).inflate(R.layout.change_product_qty, null);
        val alert: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(qtyView)
            .setPositiveButton("Да", null)
            .setNegativeButton("Нет", null)
            .create()

        alert.setOnShowListener {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val qty = qtyView.findViewById<EditText>(R.id.quantity)
                if (!qty.text.equals("")) {
                    list[position].pivot.quantity = qty.text.toString().toInt()
                    order.products = list
                    viewModel.setActiveOrder(order)
                    alert.dismiss()
                }
            }
        }
        alert.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}