package io.github.dsouzadyn.evently

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.zxing.Result

import kotlinx.android.synthetic.main.activity_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    data class Acknowledgement(val n: Int, val nModified: Int, val ok: Int) {
        class Deserializer: ResponseDeserializable<Acknowledgement> {
            override fun deserialize(content: String) = Gson().fromJson(content, Acknowledgement::class.java)
        }
    }

    var mScannerView : ZXingScannerView? = null
    var mCameraId: Int? = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        //ask for authorisation
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 50);
        else
            mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }
    override fun handleResult(p0: Result?) {
        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
        FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
        val progDialog = indeterminateProgressDialog("Confirming...")
        if(p0?.barcodeFormat.toString() == "QR_CODE") {
            progDialog.show()
            val url = p0?.text
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
            url!!.httpPost()
                    .body("confirmed=true").responseObject(Acknowledgement.Deserializer()) {_, _, result ->
                val (ack, error) = result
                if(error == null) {
                    progDialog.dismiss()
                    alert("Successfully confirmed!").show()
                } else {
                    progDialog.dismiss()
                    alert(error.message!!).show()
                }
            }
        }
        // If you would like to resume scanning, call this method below:
        // mScannerView?.resumeCameraPreview(this)
    }
}
