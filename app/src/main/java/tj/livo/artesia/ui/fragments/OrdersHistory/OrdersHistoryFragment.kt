package tj.livo.artesia.ui.fragments.OrdersHistory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import okhttp3.internal.Util
import tj.livo.artesia.R
import tj.livo.artesia.Utils
import tj.livo.artesia.adapters.OrderHistoryAdapter
import tj.livo.artesia.databinding.OrdersHistoryFragmentBinding
import tj.livo.artesia.models.Order

class OrdersHistoryFragment : Fragment(), OrderHistoryAdapter.OrderCall {


    private lateinit var viewModel: OrdersHistoryViewModel
    private lateinit var binding: OrdersHistoryFragmentBinding
    private lateinit var orderAdapter : OrderHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.orders_history_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.getRoot()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(OrdersHistoryViewModel::class.java)
        orderAdapter = OrderHistoryAdapter(this,this)
        binding.recyclerOrderHistory.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerOrderHistory.adapter = orderAdapter
        viewModel.orders().observe(this, Observer {
            it.let {
                Log.e("PROCCES1",it.size.toString())
                orderAdapter.setData(it.toMutableList())
            }
        })


    }

    override fun itemClickListener(phone: String) {
        Utils.callTo(this,phone)
    }

    override fun addToOrder(order: Order) {

        viewModel.addOrderToQueue(order)
    }

    override fun removeFromOrder(order: Order) {
        if (order.isActive){
            Toast.makeText(requireContext(),"Заказ является активным",Toast.LENGTH_LONG).show()

        }else{
            viewModel.removeOrderFromQueue(order)
        }

    }

    override fun onResume() {
        viewModel.getOrders()
        viewModel.orders().observe(viewLifecycleOwner) {
            it.let {
                orderAdapter.setData(it.toMutableList())
            }
        }
        super.onResume()
    }
}