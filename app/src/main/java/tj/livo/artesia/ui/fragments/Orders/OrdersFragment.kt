package tj.livo.artesia.ui.fragments.Orders

import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import tj.livo.artesia.R
import tj.livo.artesia.Utils
import tj.livo.artesia.adapters.OrderHistoryAdapter
import tj.livo.artesia.databinding.OrdersFragmentBinding
import tj.livo.artesia.models.Order
import tj.livo.artesia.services.shitCode.TimeService
import tj.livo.artesia.ui.fragments.OrdersHistory.OrdersHistoryViewModel

class OrdersFragment : Fragment(), OrderHistoryAdapter.OrderCall {
    private lateinit var viewModel: OrdersHistoryViewModel
    private lateinit var binding: OrdersFragmentBinding
    private lateinit var queueAdapter : OrderHistoryAdapter
    private var hasAnyActive : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.orders_fragment,container,false)
        return binding.getRoot()
    }

    override fun onResume() {
        viewModel.getOrders()
        viewModel.getQueue().observe(viewLifecycleOwner) {
            it.let {
                queueAdapter.setData(it.toMutableList())
            }
        }
        super.onResume()
    }

    fun startOrder(it : List<Order>){
        if (it.isNotEmpty()){
            binding.startOrder.setBackgroundColor(Color.RED)
            binding.startOrder.text = "ОТМЕНИТЬ ДОСТАВКУ"
            hasAnyActive = true
            binding.startOrder.setOnClickListener {
                queueAdapter.products.forEachIndexed { index, order ->
                    order.inProcces = false
                    order.isActive = false
                    viewModel.setDisActiveOrder(order)
                    val timer = Intent(requireActivity(), TimeService::class.java)
                    requireActivity().stopService(timer)
                }
            }
        }else{
            hasAnyActive = false
            binding.startOrder.setOnClickListener {
                if (queueAdapter.products.isNotEmpty()){
                    queueAdapter.products.forEachIndexed { index, order ->
                        order.inProcces = true
                        order.isActive = true
                        viewModel.setActiveOrder(order)
                        val timer = Intent(requireActivity(), TimeService::class.java)
                        requireActivity().startService(timer)
                        requireActivity().findViewById<ViewPager2>(R.id.pager).setCurrentItem(2)
                    }
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(OrdersHistoryViewModel::class.java)
        queueAdapter = OrderHistoryAdapter(this,this)
        viewModel.getQueue().observe(viewLifecycleOwner, Observer {
            queueAdapter.setData(it.toMutableList())
        })

        binding.recyclerQueue.layoutManager = GridLayoutManager(requireContext(),1)
        binding.recyclerQueue.adapter = queueAdapter
        startOrder(queueAdapter.products)
    }

    override fun itemClickListener(phone: String) {
        Utils.callTo(this,phone)
    }

    override fun addToOrder(order: Order) {
        viewModel.addOrderToQueue(order)
    }

    override fun removeFromOrder(order: Order) {
        viewModel.removeOrderFromQueue(order)
    }

}