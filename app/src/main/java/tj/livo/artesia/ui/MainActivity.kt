package tj.livo.artesia.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import tj.livo.artesia.R
import tj.livo.artesia.SessionManager
import tj.livo.artesia.services.GetOrderService
import tj.livo.artesia.ui.fragments.ActiveOrders.ActiveOrdersFragment
import tj.livo.artesia.ui.fragments.Orders.OrdersFragment
import tj.livo.artesia.ui.fragments.OrdersHistory.OrdersHistoryFragment
import tj.livo.artesia.ui.fragments.OrdersHistory.OrdersHistoryViewModel
import tj.livo.artesia.ui.fragments.login.LoginActivity
import tj.livo.artesia.ui.fragments.login.LoginViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var ordersHistoryViewModel: OrdersHistoryViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        Log.e("Key",sessionManager.getToken().toString())
        loginViewModel = ViewModelProvider(this,ViewModelProvider
            .AndroidViewModelFactory(application)).get(LoginViewModel::class.java)
        ordersHistoryViewModel = ViewModelProvider(this,ViewModelProvider
            .AndroidViewModelFactory(application)).get(OrdersHistoryViewModel::class.java)
        viewPager = findViewById(R.id.pager)


        val getOrderService = Intent(this, GetOrderService::class.java)
        startService(getOrderService)
        viewPager.adapter = FragmentAdapters(supportFragmentManager, this.lifecycle)
        TabLayoutMediator(tab_layout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Заказы"
                1 -> "Очередь"
                else -> "Активные"
            }
        }.attach()
    }

    class FragmentAdapters(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            Log.e("POSTION",position.toString())
            when (position) {
                0 -> {
                    return OrdersHistoryFragment()
                }
                1 -> {
                    return OrdersFragment()
                }
                2 -> {
                    return ActiveOrdersFragment()
                }
            }
            return OrdersHistoryFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.actionbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.exit->{

                loginViewModel.logout().observe(this){
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    sessionManager.deleteAll()
                }
            }
            R.id.reload->{
                this.recreate()
            }
        }
        return true
    }
}