package com.jgur.starterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Creates the ViewLog view which will show any items logged for the selected date.
 */
class ViewLog : AppCompatActivity() {



    /**
     * Loads the data from the local file each time the view is opened
     */
    override fun onResume() {
        super.onResume()
        loadFile()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_log)
        //Loads local data to display when intent is started.
        loadFile();

//        val bfastValue = findViewById<TextView>(R.id.breakfastValues)
//        val lunchValue = findViewById<TextView>(R.id.lunchValues)
//        val dinnerValue = findViewById<TextView>(R.id.dinnerValues)
//        bfastValue.setMovementMethod(ScrollingMovementMethod())
//        lunchValue.setMovementMethod(ScrollingMovementMethod())
//        dinnerValue.setMovementMethod(ScrollingMovementMethod())

        val addItemToLog: Button = findViewById(R.id.addItemToLog)
        val homeButton: Button = findViewById(R.id.homeButton)

        //Goes to the add item page when button is clicked
        addItemToLog.setOnClickListener(View.OnClickListener {
            goAddItem()
        })

        //Goes to the home page when button is clicked.
        homeButton.setOnClickListener(View.OnClickListener {
            goHome();
        })

    }

    /**
     * Loads a file from local storage.
     */
    private fun loadFile() {
        val text = readFromFile(applicationContext)
        parseText(text)
    }

    /**
     * Logic for reading from a local file
     */
    private fun readFromFile(context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("config.txt")
            if (inputStream != null) {
                val inputStreamReader =
                        InputStreamReader(inputStream)
                val bufferedReader =
                        BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
            Log.d("Loaded file", ret)
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }

    /**
     * Parses the data from the local files for the given date
     * @param fileText - File from local storage containing all item data
     */
    private fun parseText(fileText: String) {
        val dateValue = intent.getStringExtra("date")
        val regex = Regex("DATE:"+dateValue+":(.*?):END:")
        val matches = regex.findAll(fileText)
        val itemsInputted = matches.map { it.groupValues[0] }.joinToString()
        setValues(itemsInputted)
    }

    /**
     * Displays each item for the given meal for the selected date
     * @param fileText - Text pared from local storage containing
     * only information for the given data.
     */
    private fun setValues(fileText: String) {
        val bfastValue = findViewById<TextView>(R.id.breakfastValues)
        val lunchValue = findViewById<TextView>(R.id.lunchValues)
        val dinnerValue = findViewById<TextView>(R.id.dinnerValues)

        val bfastRegex = Regex("ITEMB:(.*?):MEAL:Breakfast:")
        val bfastMatches = bfastRegex.findAll(fileText)
        var bfastInput = bfastMatches.map { it.groupValues[1] }.joinToString().replace(",", "\n")
        bfastValue.setText(" " + bfastInput)

        val lunchRegex = Regex("ITEML:(.*?):MEAL:Lunch:")
        val lunchMatches = lunchRegex.findAll(fileText)
        val lunchInput = lunchMatches.map { it.groupValues[1] }.joinToString().replace(",", "\n")
        lunchValue.setText(" " +lunchInput)

        val dinnerRegex = Regex("ITEMD:(.*?):MEAL:Dinner:")
        val dinnerMatches = dinnerRegex.findAll(fileText)
        val dinnerInput = dinnerMatches.map { it.groupValues[1] }.joinToString().replace(",", "\n")
        dinnerValue.setText(" " + dinnerInput)
    }

    /**
     * Goes to home screen, if it is already initialized it will
     * get refreshed.
     */
    private fun goHome(){
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent, 0);
    }

    /**
     * Goes to the add item screen.
     */
    private fun goAddItem(){
        val intent = Intent(this, InputItems::class.java).apply {
        }
        startActivity(intent)
    }

}