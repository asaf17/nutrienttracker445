package com.jgur.starterapp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
    Creates the main activity which will access daily data and display calories for the day.
    From here you can view the items you have previusly logged or add more items to the log
 */
class MainActivity : AppCompatActivity() {

    val myCalendar = Calendar.getInstance()

    override fun onResume() {
        super.onResume()
        loadFile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyStoragePermissions(this)
        loadFile()

        //Goes to the input item view when the button is clicked
        val addItemButton: Button = findViewById(R.id.addItem)
        addItemButton.setOnClickListener(View.OnClickListener {
            inputItemsButtonClick()
        })

        //Goes to the log when the button is clicked
        val viewLogButton: Button = findViewById(R.id.viewLog)
        viewLogButton.setOnClickListener(View.OnClickListener {
            viewLogButtonClick();
        })

        val dateTest = findViewById<View>(R.id.dateText) as EditText
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        dateTest.setText(sdf.format(Date()))

        val dateListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateLabel()
            loadFile()
        }

        dateTest.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(this@MainActivity, dateListener, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                        myCalendar[Calendar.DAY_OF_MONTH]).show();
            }
        })

    }

    /**
     * Loads the local file that we have written to from the device
     *  and parses it for information.
     */
    private fun loadFile() {
        val text = readFromFile(applicationContext)
        val date = findViewById<TextView>(R.id.dateText);
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

    //Ensures the application has proper read/write permissions.
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                112
            )
        }
    }

    /**
     * Opens the InputItems view when the button is pressed.
     */
    private fun inputItemsButtonClick(){
        val intent = Intent(this, InputItems::class.java).apply {
        }
        val date = findViewById<TextView>(R.id.dateText);
        intent.putExtra("date", date.text.toString())
        startActivity(intent)
    }

    /**
     * Opens the ViewLog view when the button is pressed.
     */
    private fun viewLogButtonClick(){
        val intent = Intent(this, ViewLog::class.java).apply {
        }
        val date = findViewById<TextView>(R.id.dateText);
        intent.putExtra("date", date.text.toString())
        startActivity(intent)
    }

    /**
     * Parses the text from the local file to find
     * only information for the selected date.
     * @param fileText - String data from local file
     */
    private fun parseText(fileText: String) {
//        val sdf = SimpleDateFormat("MM/dd/yyyy")
//        val date =  sdf.format(Date())

//        val myFormat = "MM/dd/yyyy"
//        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val date = findViewById<TextView>(R.id.dateText);
        val dateString = date.text.toString()

        val regex = Regex("DATE:"+dateString+":(.*?):END:")
        val matches = regex.findAll(fileText)
        val itemsInputted = matches.map { it.groupValues[0] }.joinToString()
        setValues(itemsInputted)
    }

    /**
     * Sets the various values accorind t
     * @param fileText Parsed data from the local file with only
     * the selected date we are looking at
     */
    private fun setValues(fileText: String) {
        val calsValue = findViewById<TextView>(R.id.caloriesText)
        val protValue = findViewById<TextView>(R.id.proteinText)
        val carbValue = findViewById<TextView>(R.id.carbsText)
        val fatValue = findViewById<TextView>(R.id.fatText)
        val sugarValue = findViewById<TextView>(R.id.sugarText)

        val calRegex = Regex("CAL:(\\d+):PRO")
        val calMatches = calRegex.findAll(fileText)
        var calsInput = calMatches.map { it.groupValues[1] }.joinToString().replace(", ", "+")
        calsInput = findSum(calsInput).toString()
        calsValue.setText("Calories (kcal): " + calsInput)

        val protRegex = Regex("PRO:(\\d+):CARB")
        val protMatches = protRegex.findAll(fileText)
        var protInput = protMatches.map { it.groupValues[1] }.joinToString().replace(", ", "+")
        protInput = findSum(protInput).toString()
        protValue.setText("Protein (g): " + protInput)

        val carbRegex = Regex("CARB:(\\d+):FAT")
        val carbMatches = carbRegex.findAll(fileText)
        var carbInput = carbMatches.map { it.groupValues[1] }.joinToString().replace(", ", "+")
        carbInput = findSum(carbInput).toString()
        carbValue.setText("Carbs (g): " + carbInput)

        val fatRegex = Regex("FAT:(\\d+):SUG")
        val fatMatches = fatRegex.findAll(fileText)
        var fatInput = fatMatches.map { it.groupValues[1] }.joinToString().replace(", ", "+")
        fatInput = findSum(fatInput).toString()
        fatValue.setText("Fats (g): " + fatInput)

        val sugRegex = Regex("SUG:(\\d+):END:")
        val sugMatches = sugRegex.findAll(fileText)
        var sugInput = sugMatches.map { it.groupValues[1] }.joinToString().replace(", ", "+")
        sugInput = findSum(sugInput).toString()
        sugarValue.setText("Sugars (g): " + sugInput)

    }

    /**
     * Gets sum of a given string
     */
    fun findSum(str: String): Int {
        var temp = "0"
        var sum = 0

        // read each character in input string
        for (i in 0 until str.length) {
            val ch = str[i]

            if (Character.isDigit(ch)) temp += ch else {
                sum += temp.toInt()
                temp = "0"
            }
        }
        return sum + temp.toInt()
    }


    private fun updateLabel() {
        val dateText = findViewById<View>(R.id.dateText) as EditText
        val myFormat = "MM/dd/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateText.setText(sdf.format(myCalendar.getTime()))
    }





}