package com.example.singletonpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var curencyDetails: Datum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //xml UI element
        val editText = findViewById<View>(R.id.editText) as EditText
        val button = findViewById<View>(R.id.button) as Button
        val spinner = findViewById<View>(R.id.spinner1) as Spinner
        //list added it in spinner
        //val currency = arrayListOf("inr", "usd", "aud", "sar", "cny", "jpy")
        val currency = Constants.CURRENCY_ARRAY
        // selected item from drop-down menu
        var selected: Int = 0

        if (spinner != null) {
            //fill spinner
            val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item, currency
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                ) {
                    selected = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // do nothing
                }
            }
        }
        // handle convert button
        button.setOnClickListener {
            //get number from edit text and convert it to double
            var value = editText.text.toString().toDouble()

            //invoke get currency method
            getCurrency(onResult = {
                curencyDetails = it
                when (selected) {
                    0 -> display(converter(curencyDetails?.eur?.inr?.toDouble(), value));
                    1 -> display(converter(curencyDetails?.eur?.usd?.toDouble(), value));
                    2 -> display(converter(curencyDetails?.eur?.aud?.toDouble(), value));
                    3 -> display(converter(curencyDetails?.eur?.sar?.toDouble(), value));
                    4 -> display(converter(curencyDetails?.eur?.cny?.toDouble(), value));
                    5 -> display(converter(curencyDetails?.eur?.jpy?.toDouble(), value));
                }
            })
            //date.text = date.text.toString() +" 2021-10-04"
        }

    }

    private fun display(result: Double) {
        //UI element
        val responseText = findViewById<View>(R.id.output) as TextView
        val date = findViewById<View>(R.id.date) as TextView
        //Date
        var today = curencyDetails?.date
        //print result and date
        responseText.text = "result: " + result
        date.text = "Date: ${today}"
    }

    // convert currency
    private fun converter(i: Double?, sel: Double): Double {
        var s = 0.0
        if (i != null) {
            s = (i * sel)
        }
        return s
    }

    //get currency method
    private fun getCurrency(onResult: (Datum?) -> Unit) {
        //APIInterface
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        if (apiInterface != null) {
            apiInterface.getCurrency()?.enqueue(object : Callback<Datum> {
                override fun onResponse(
                        call: Call<Datum>,
                        response: Response<Datum>
                ) {
                    onResult(response.body())

                }

                override fun onFailure(call: Call<Datum>, t: Throwable) {
                    onResult(null)
                    Toast.makeText(applicationContext, "" + t.message, Toast.LENGTH_SHORT).show();
                }

            })
        }
    }
}