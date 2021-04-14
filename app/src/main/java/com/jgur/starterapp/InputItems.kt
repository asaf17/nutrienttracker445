package com.jgur.starterapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*


class InputItems : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_item)

        val addItemToLog: Button = findViewById(R.id.addItemToLog)

        //Creates a spinner for each of the different meal options
        val mealSpinner: Spinner = findViewById(R.id.mealSpinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.mealsArray,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked)
            mealSpinner.adapter = adapter
        }


        //Creates a spinner for each of the different unit options
        val unitSpinner: Spinner = findViewById(R.id.unitSpinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.unitsArray,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked)
            unitSpinner.adapter = adapter
        }

        //When an item is added to the log it will save it to a local file on the device
        addItemToLog.setOnClickListener(View.OnClickListener {
            saveFile();
        })

    }

    /**
     * Gets all the information that the user has inputted and saves
     * it to the local device storage.
     */
    fun saveFile() {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val date = (sdf.format(Date()))
        val itemName: TextInputEditText = findViewById(R.id.itemName);
        val quantity: TextInputEditText = findViewById(R.id.quantityField)
        val unitSpinner: Spinner = findViewById(R.id.unitSpinner);
        val unit = unitSpinner.selectedItem.toString()
        val caloriesNumber: EditText = findViewById(R.id.caloriesInput)
        val proteinNumber: EditText = findViewById(R.id.proteinInput)
        val carbsNumber: EditText = findViewById(R.id.carbsInput)
        val fatNumber: EditText = findViewById(R.id.fatInput)
        val sugarsNumber: EditText = findViewById(R.id.sugarInput)
        val mealSpinner: Spinner = findViewById(R.id.mealSpinner);
        val meal = mealSpinner.selectedItem.toString()

        var text = ""

        if(meal == "Breakfast"){
            text = "DATE:" + date + ":ITEMB:" + itemName.text.toString() + " - " +
                    quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                    caloriesNumber.text + ":PRO:" + proteinNumber.text + ":CARB:" +
                    carbsNumber.text + ":FAT:" + fatNumber.text + ":SUG:" + sugarsNumber.text + ":END:"
        }
        else if(meal == "Lunch"){
            text = "DATE:" + date + ":ITEML:" + itemName.text.toString() + " - " +
                    quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                    caloriesNumber.text + ":PRO:" + proteinNumber.text + ":CARB:" +
                    carbsNumber.text + ":FAT:" + fatNumber.text + ":SUG:" + sugarsNumber.text + ":END:"
        } else{
            text = "DATE:" + date + ":ITEMD:" + itemName.text.toString() + " - " +
                    quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                    caloriesNumber.text + ":PRO:" + proteinNumber.text + ":CARB:" +
                    carbsNumber.text + ":FAT:" + fatNumber.text + ":SUG:" + sugarsNumber.text + ":END:"
        }

        savedToastMsg("Added Item to Log");
        writeToFile(text, applicationContext)
    }

    /**
     * Logic for writing to a file.
     */
    private fun writeToFile(
            data: String,
            context: Context
    ) {
        Log.d("writeToFile", data)
        try {
            val outputStreamWriter = OutputStreamWriter(
                    context.openFileOutput(
                            "config.txt",
                            Context.MODE_APPEND
                            //Context.MODE_PRIVATE
                    )
            )
            outputStreamWriter.write(data)
            outputStreamWriter.close()
            Log.d("Saved file", data)
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun savedToastMsg(msg: String?) {
        val savedtoast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        savedtoast.show()
    }

}



//        val itemName: EditText = findViewById(R.id.itemName)
//        itemName.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//            }
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//            }
//            override fun afterTextChanged(s: Editable) {
//
//            }
//        })



