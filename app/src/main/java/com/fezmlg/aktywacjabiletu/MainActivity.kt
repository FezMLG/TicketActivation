package com.fezmlg.aktywacjabiletu

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


class MainActivity : AppCompatActivity() {
    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    private lateinit var qrCode: ImageView
    private lateinit var dataEdt: EditText
    private lateinit var generateQrBtn: Button
    lateinit var radioMetro1: RadioButton
    private lateinit var radioMetro2: RadioButton
    private lateinit var radioBus: RadioButton
    private lateinit var radioTram: RadioButton
    lateinit var transportInfo: TextView
    private lateinit var radioTransport: RadioGroup
    private var bitmap: Bitmap? = null
    lateinit var station: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing all variables.
        qrCode = findViewById(R.id.idQrCode)
        dataEdt = findViewById(R.id.idEdit)
        generateQrBtn = findViewById(R.id.idBtnGenerateQR)
        radioMetro1 = findViewById(R.id.idRadioMetro1)
        radioMetro2 = findViewById(R.id.idRadioMetro2)
        radioBus = findViewById(R.id.idRadioBus)
        radioTram = findViewById(R.id.idRadioTram)
        radioTransport = findViewById(R.id.idRadioTransport)
        transportInfo = findViewById(R.id.idTransportInfo)
        station = findViewById(R.id.idStation)

        // initializing onclick listener for radio group
        radioTransport.setOnCheckedChangeListener { _, _ ->
            // If the radiobutton that has changed in check state is now checked...
            when {
                radioMetro1.isChecked -> {
                    transportInfo.text = getString(R.string.radioMetro_info)
                    generateDataForSpinner(R.array.m1_stops)
                    station.visibility = View.VISIBLE
                    dataEdt.visibility = View.GONE
                }
                radioMetro2.isChecked -> {
                    transportInfo.text = getString(R.string.radioMetro_info)
                    generateDataForSpinner(R.array.m2_stops)
                    station.visibility = View.VISIBLE
                    dataEdt.visibility = View.GONE
                }
                radioBus.isChecked -> {
                    transportInfo.text = getString(R.string.radioBus_info)
                    station.visibility = View.GONE
                    dataEdt.visibility = View.VISIBLE
                }
                radioTram.isChecked -> {
                    transportInfo.text = getString(R.string.radioTram_info)
                    station.visibility = View.GONE
                    dataEdt.visibility = View.VISIBLE
                }
            }
        }

        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener {
            if (radioBus.isChecked && dataEdt.text.length != 4) {

                // if none of radio's is clicked
                Toast.makeText(
                    this@MainActivity,
                    "Nieprawidłowy numer boczny autobusu",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (radioTram.isChecked && dataEdt.text.length != 4) {

                // if none of radio's is clicked
                Toast.makeText(
                    this@MainActivity,
                    "Nieprawidłowy numer boczny tramwaju",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                // generating string for qr code
                var activateCode = "WTPWarszawa_"
                when {
                    radioMetro1.isChecked -> {
                        val temp = getCodeByStation(station.selectedItem.toString(), "M1")
                        activateCode += "M191$temp"
                    }
                    radioMetro2.isChecked -> {
                        val temp = getCodeByStation(station.selectedItem.toString(), "M2")
                        activateCode += "M192$temp"
                    }
                    radioBus.isChecked -> {
                        activateCode += "B"
                    }
                    radioTram.isChecked -> {
                        activateCode += "T"
                    }
                }
                activateCode += dataEdt.text.toString()

                // checking if any of radio's are chosen and making a qr code
                if (radioTransport.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        this@MainActivity,
                        "Zaznacz typ transportu",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        // getting our qrcode in the form of bitmap.
                        bitmap = getQrCodeBitmap(activateCode)
                        // the bitmap is set inside our image
                        // view using .setimagebitmap method.
                        qrCode.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        // this method is called for
                        // exception handling.
                        Log.e("Tag", e.toString())
                    }
                }
            }
        }

    }

    private fun generateDataForSpinner(dataSource: Int){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            dataSource,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            station.adapter = adapter
        }
    }


    private fun getQrCodeBitmap(qrCodeContent: String): Bitmap {
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val size = qrCode.width
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    private fun getCodeByStation(name: String, line: String): String? {
        var i = -1
        var station = 0
        var key = 0
        if (line == "M1"){
            station = R.array.m1_stops
            key = R.array.m1_stops_keys
        } else if (line == "M2") {
            station = R.array.m2_stops
            key = R.array.m2_stops_keys
        }
        for (cc in resources.getStringArray(station)) {
            i++
            if (cc == name) break
        }
        return resources.getStringArray(key)[i]
    }
}