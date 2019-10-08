package io.github.dsouzadyn.evently

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.auth0.android.jwt.JWT

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import io.github.dsouzadyn.evently.models.Event
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast


class LoginActivity : AppCompatActivity() {

    private val APP_TAG = "LOGIN_ACTIVITY"
    private val REQUEST_SIGNUP = 0

    data class Event(val id: String, val name: String, val description: String = "", val capacity: Int,
                     val start_time: String, val end_time: String,
                     val start_time2: String, val end_time2: String,
                     val start_time3: String, val end_time3: String,
                     val price: Float = 0.0f, val type: String, val subtype: String, val location: String, val cumpolsory: Boolean = false) {
        class Deserializer: ResponseDeserializable<List<Event>> {
            override fun deserialize(content: String): List<Event>? = Gson().fromJson(content, Array<Event>::class.java).toList()
        }
    }

    data class Checker(val id: String, val is_valid: Boolean = true) {
        class Deserializer: ResponseDeserializable<Checker> {
            override fun deserialize(content: String): Checker? = Gson().fromJson(content, Checker::class.java)
        }
    }
    data class User(val email: String= "", val id: String = "", val roll_number: Int, val semester: Int, val username: String = "",val confirmed: Boolean, val role: Int, val name: String = "", val number:String = "", val events: List<Event>, val checker: Checker)
    data class Token(val jwt: String = "", val user: User, val message: String) {
        class Deserializer: ResponseDeserializable<Token> {
            override fun deserialize(content: String): Token? = Gson().fromJson(content, Token::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            loginHandler()
        }
        signUpLink.setOnClickListener {
            signUpLinkHandler()
        }




    }



    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                longToast("User Registered Successfully")
            }
        }
    }

    private fun loginHandler() {
        loginBtn.isEnabled = false
        if(!validate()) {
            onLoginFailed()
            return
        }
        val progressDialog = indeterminateProgressDialog("Loading...")
        progressDialog.show()
        Log.wtf("url is", getString(R.string.base_api_url))
        "${getString(R.string.temp_url)}/auth/local".httpPost()           //changed here
                .body("identifier=${loginEmail.text.toString()}&password=${loginPassword.text.toString()}")
                .responseObject(Token.Deserializer()) {r, re, result ->
                    val (token, error) = result
                    Log.wtf("error is", error?.toString())
                    if (error == null && token?.user?.checker != null && !token?.user?.checker?.is_valid!!) {
                        progressDialog.dismiss()
                        alert("Please validate your mail address by visiting the link we sent to you.").show()
                        onLoginFailed()
                    } else if (error == null && token?.message != "Bad Request") {

                        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString(getString(R.string.token_key), token?.jwt)
                        var tempId = token?.user?.id
                        editor.putString(getString(R.string.uid_key), tempId)


                        editor.putString(getString(R.string.uname_key), token?.user?.name)
                        if(token?.user?.events!!.isNotEmpty()) {
                            token.user.events.forEach { e ->
                                database.use {
                                    insert(
                                            io.github.dsouzadyn.evently.models.Event.TABLE_NAME,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_ID to e.id,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_NAME to e.name,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_PRICE to e.price,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_UID to token.user.id,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_LOCATION to e.location,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_START_TIME to e.start_time,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_START_TIME2 to e.start_time2,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_START_TIME3 to e.start_time3,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_END_TIME to e.end_time,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_END_TIME2 to e.end_time2,
                                            io.github.dsouzadyn.evently.models.Event.COLUMN_END_TIME3 to e.end_time3
                                    )
                                }
                            }
                        }
                        if(token?.user?.confirmed!!)
                            editor.putString(getString(R.string.conf_key), "CONF")
                        editor.putInt(getString(R.string.urole_key), token?.user?.role!!)
                        editor.apply()
                        progressDialog.dismiss()
                        onLoginSuccess()
                    } else {
                        Log.wtf("error is", error.toString())
                        progressDialog.dismiss()
                        onLoginFailed()
                    }
                }
    }



    private fun signUpLinkHandler() {
        intent = Intent(applicationContext, SignUpActivity::class.java)
        startActivityForResult(intent, REQUEST_SIGNUP)
    }

    private fun onLoginFailed() {
        longToast("Failed to Login")
        loginBtn.isEnabled = true
    }

    private fun onLoginSuccess() {
        this.setResult(420)
        loginBtn.isEnabled = true
        this.finish()
    }

    private fun validate(): Boolean {

        var valid = true

        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.error = "Enter a valid email address"
            valid = false
        } else {
            loginEmail.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 16) {
            loginPassword.error = "The password should be between 4 and 16 characters"
            valid = false
        } else {
            loginPassword.error = null
        }

        return valid
    }
}
