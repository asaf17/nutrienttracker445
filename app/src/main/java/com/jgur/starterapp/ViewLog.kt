package com.jgur.starterapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        loadFile();
    }

    private fun loadFile() {
        val text = readFromFile(applicationContext)
        parseText(text)
    }

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

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val date =  sdf.format(Date())
        val regex = Regex("DATE:"+date+":(.*):END:")
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

        val bfastRegex = Regex("ITEM:(.+):MEAL:Breakfast:")
        val bfastMatches = bfastRegex.findAll(fileText)
        val bfastInput = bfastMatches.map { it.groupValues[1] }.joinToString()
        bfastValue.setText(bfastInput)

        val lunchRegex = Regex("ITEM:(.+):MEAL:Lunch:")
        val lunchMatches = lunchRegex.findAll(fileText)
        val lunchInput = lunchMatches.map { it.groupValues[1] }.joinToString()
        lunchValue.setText(lunchInput)

        val dinnerRegex = Regex("ITEM:(.+):MEAL:Dinner:")
        val dinnerMatches = dinnerRegex.findAll(fileText)
        val dinnerInput = dinnerMatches.map { it.groupValues[1] }.joinToString()
        dinnerValue.setText(dinnerInput)
    }


    /*
    This will have the log of all data. It will have a calendar which allows you
    to select what data you want to see. It will display the items eaten
    and their caloric values for breakfast, lunch, and dinner.
     */

}