package com.fezmlg.aktywacjabiletu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


class MainActivity : AppCompatActivity() {
    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    lateinit var qrCode: ImageView
    lateinit var dataEdt: EditText
    private lateinit var generateQrBtn: Button
    lateinit var radioMetro: RadioButton
    lateinit var radioBusTram:RadioButton
    lateinit var transportInfo: TextView
    private lateinit var radioTransport: RadioGroup
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing all variables.
        qrCode = findViewById(R.id.idQrCode)
        dataEdt = findViewById(R.id.idEdit)
        generateQrBtn = findViewById(R.id.idBtnGenerateQR)
        radioMetro = findViewById(R.id.idRadioMetro)
        radioBusTram = findViewById(R.id.idRadioBusTram)
        radioTransport = findViewById(R.id.idRadioTransport)
        transportInfo = findViewById(R.id.idTransportInfo)

        // initializing onclick listener for radio group
        radioTransport.setOnCheckedChangeListener { _, _ ->
            // If the radiobutton that has changed in check state is now checked...
            if (radioMetro.isChecked) {
                transportInfo.text = getString(R.string.radioMetro_info)
            } else if (radioBusTram.isChecked) {
                transportInfo.text = getString(R.string.radioBusTram_info)
            }
        }

        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener {
            if (TextUtils.isEmpty(dataEdt.text.toString())) {

                // if the edittext inputs are empty then execute
                // this method showing a toast message.
                Toast.makeText(
                    this@MainActivity,
                    "Wpisz numer, aby wygenerować kod QR",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (radioBusTram.isChecked && dataEdt.text.length != 4) {

                // if none of radio's is clicked
                Toast.makeText(
                    this@MainActivity,
                    "Nieprawidłowy numer boczny",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // generating string for qr code
                var activateCode = "WTPWarszawa_"
                if (radioMetro.isChecked) {
                    activateCode += "M19"
                } else if (radioBusTram.isChecked) {
                    activateCode += "B"
                }
                activateCode += dataEdt.text.toString()

                // checking if any of radio is choosed and making a qr code
                if (radioTransport.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        this@MainActivity,
                        "Zaznacz typ transportu",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        // getting our qrcode in the form of bitmap.
                        bitmap = GetQrCodeBitmap(activateCode)
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


    fun GetQrCodeBitmap(qrCodeContent: String): Bitmap {
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

}