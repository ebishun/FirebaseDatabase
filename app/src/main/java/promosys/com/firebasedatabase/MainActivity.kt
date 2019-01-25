package promosys.com.firebasedatabase

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRegistrar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build()
        //AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                REQUEST_CODE_SIGN_IN
            )
        }

        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this@MainActivity,AuntheticatedActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_SIGN_IN){
            if (resultCode == Activity.RESULT_OK){
                startActivity(Intent(this@MainActivity,AuntheticatedActivity::class.java))
            }else{
                Toast.makeText(this,"Log in failed, please try again",Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1234
    }

}
