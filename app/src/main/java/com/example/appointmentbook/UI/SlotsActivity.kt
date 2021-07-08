package com.example.appointmentbook.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appointmentbook.Network.ApiAdapter
import com.example.appointmentbook.R
import com.example.appointmentbook.UI.Adapter.SlotsAdapter
import com.example.appointmentbook.UI.Login.User.UserLoginActivity
import com.example.appointmentbook.data.SlotsData
import com.example.appointmentbook.utils.Utils.Companion.USER_NAME
import com.example.appointmentbook.utils.Utils.Companion.getPreference
import com.example.appointmentbook.utils.Utils.Companion.getUserName
import com.example.appointmentbook.utils.Utils.Companion.logout
import kotlinx.android.synthetic.main.activity_slots.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SlotsActivity : AppCompatActivity() {

    private val slotsAdapter by lazy {
        SlotsAdapter().apply {
            btnBookSlot = itemClicked
        }
    }

    private val itemClicked = { position: Int, data: SlotsData ->
        //handle item clicked
        bookSlot(data.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slots)
        getData()
        supportActionBar?.hide()

        slotProgressBar.visibility = View.VISIBLE
        slotsRecycler.visibility = View.INVISIBLE

        slotsRecycler.apply {
            adapter = slotsAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SlotsActivity)
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = ApiAdapter.apiClient.slotAvailable()
                if (response.isSuccessful && response.body() != null) {
                    slotProgressBar.visibility = View.INVISIBLE
                    slotsRecycler.visibility = View.VISIBLE
                    slotsAdapter.list = response.body() as ArrayList<SlotsData>
                    slotsAdapter.notifyDataSetChanged() //this line can be invoked from about code, inside adapter class
                } else {
                    Toast.makeText(
                        this@SlotsActivity,
                        response.message().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SlotsActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        loggedInUserName.setOnClickListener {
            bookRequests()
        }

        btnLogout.setOnClickListener {
            logout()
            /**
             * instead we can clear shared preferences
             * clearPreferences()
             */
            //clearPreferences()
            startActivity(Intent(this, UserLoginActivity::class.java))
            finish()
        }
    }

    private fun bookSlot(id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val getPref = getPreference()
                val type = getPref.getString("type", "")
                val token = getPref.getString("token", "") //todo create function to get token
                val response = ApiAdapter.apiClient.bookSlot(
                    "$type $token",
                    id
                )
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()!!.message ?: "Booking request placed !"
                    //request placed
                    //show message or inform
                    informUser(message)
                    return@launch
                }
                showToast("Something went wrong ! Try after sometime.")
            } catch (ex: Exception) {
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun informUser(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            //let's show user's requests
            startActivity(Intent(this, BookReqActivity::class.java))
        }
    }

    @SuppressLint("WrongConstant")
    private fun bookRequests() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val getPref = getPreference()
                val type = getPref.getString("type", "")
                val token = getPref.getString("token", "") //todo create function to get token
                val response = ApiAdapter.apiClient.bookReq("$type $token")
                if (response.isSuccessful && response.body() != null) {
                    startActivity(Intent(this@SlotsActivity, BookReqActivity::class.java))
                } else {
                    Toast.makeText(
                        this@SlotsActivity,
                        response.message().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SlotsActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun getData() {
        val userName = getUserName(USER_NAME)
        loggedInUserName.text = userName
    }
}