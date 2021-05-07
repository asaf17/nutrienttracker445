package com.jgur.starterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.io.OutputStreamWriter


class InputItems : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_item)

        val addItemToLog: Button = findViewById(R.id.addItemToLogButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        val viewLog: Button = findViewById(R.id.viewLogButton)

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

        //When the home button is clicked it will go home.
        homeButton.setOnClickListener(View.OnClickListener {
            goHome()
        })

        //When the viewLog button is clicked it will go to the log.
        viewLog.setOnClickListener(View.OnClickListener {
            goViewLog()
        })


    }

    /**
     * Gets all the information that the user has inputted and saves
     * it to the local device storage.
     */
    fun saveFile() {
        val dateValue = intent.getStringExtra("date")
        val itemName: TextInputEditText = findViewById(R.id.itemName);
        val quantity: TextInputEditText = findViewById(R.id.itemQuantity)
        val unitSpinner: Spinner = findViewById(R.id.unitSpinner);
        val unit = unitSpinner.selectedItem.toString()
        val caloriesNumber: TextInputEditText = findViewById(R.id.caloriesInput)
        val proteinNumber: TextInputEditText = findViewById(R.id.proteinInput)
        val carbsNumber: TextInputEditText = findViewById(R.id.carbsInput)
        val fatNumber: TextInputEditText = findViewById(R.id.fatInput)
        val sugarsNumber: TextInputEditText = findViewById(R.id.sugarInput)
        val mealSpinner: Spinner = findViewById(R.id.mealSpinner);
        val meal = mealSpinner.selectedItem.toString()

        var text = ""

        //Checks to ensure all values are filled out, if not will give a toast message.
        if(itemName.text.toString() == "" ||  quantity.text.toString() == "" ||  caloriesNumber.text.toString() == ""||
                proteinNumber.text.toString() == "" ||  carbsNumber.text.toString() == "" || fatNumber.text.toString() == "" ||
                sugarsNumber.text.toString() == ""){
            noValueToastMsg("Fill out all fields.");

        }
        //If all values are saved it will save in a string that is easy to be
        //read in an regular expression to get values.
        else{
            if(meal == "Breakfast"){
                text = "DATE:" + dateValue + ":ITEMB:" + itemName.text.toString() + " - " +
                        quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                        caloriesNumber.text.toString() + ":PRO:" + proteinNumber.text.toString()+ ":CARB:" +
                        carbsNumber.text.toString() + ":FAT:" + fatNumber.text.toString() + ":SUG:" + sugarsNumber.text.toString() + ":END:"
            }
            else if(meal == "Lunch"){
                text = "DATE:" + dateValue + ":ITEML:" + itemName.text.toString() + " - " +
                        quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                        caloriesNumber.text.toString() + ":PRO:" + proteinNumber.text.toString()+ ":CARB:" +
                        carbsNumber.text.toString() + ":FAT:" + fatNumber.text.toString() + ":SUG:" + sugarsNumber.text.toString() + ":END:"
            } else{
                text = "DATE:" + dateValue + ":ITEMD:" + itemName.text.toString() + " - " +
                        quantity.text.toString()  + " " + unit + ":MEAL:" + meal + ":CAL:" +
                        caloriesNumber.text.toString() + ":PRO:" + proteinNumber.text.toString()+ ":CARB:" +
                        carbsNumber.text.toString() + ":FAT:" + fatNumber.text.toString() + ":SUG:" + sugarsNumber.text.toString() + ":END:"
            }

            //Resets the values for a new input.
            itemName.setText("")
            quantity.setText("")
            caloriesNumber.setText("")
            proteinNumber.setText("")
            carbsNumber.setText("")
            fatNumber.setText("")
            sugarsNumber.setText("")
            savedToastMsg("Added Item to Log");


            //Saves the data to local user storage.
            writeToFile(text, applicationContext)
        }


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

    /**
     * Toast messages
     */
    private fun savedToastMsg(msg: String?) {
        val savedtoast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        savedtoast.show()
    }

    private fun noValueToastMsg(msg: String?) {
        val novaluetoast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        novaluetoast.show()
    }

    /**
     * Method that will take the user to the home page.
     * If home page is already active it will bring it
     * back.
     */
    private fun goHome(){
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent, 0);
    }

    /**
     * Takes user to the view log and passes in the date.
     */
    private fun goViewLog(){
        val dateValue = intent.getStringExtra("date")
        val intent = Intent(this, ViewLog::class.java).apply {
        }
        intent.putExtra("date", dateValue)
        startActivity(intent)
    }


}



