package app.rishabh.firebase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import app.rishabh.firebase.utils.AppUtils;
import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements OnFailureListener {


    private static final String TAG = "Login Activity";
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.email_sign_in_button)
    Button btnSignin;
    @BindView(R.id.tv_create_account)
    TextView tvCreateAccount;
    @BindView(R.id.linear_layout)
    LinearLayout mLayoutParent;

    private final int signUpActivityRequestCode = 3;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        authListenerInitialization();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void authListenerInitialization() {
       mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged");
                if (user != null) {
                    Log.d(TAG, "check if user is verified");
                    AppUtils.getInstance().hideProgressDialog();
                    if (user.isEmailVerified()) {
                        Log.d(TAG, "user verified");
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Log.d(TAG, "user unverified");
                        AppUtils.getInstance().showToast(LoginActivity.this,"Verify your email address");
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }
        };
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        AppUtils.getInstance().hideProgressDialog();
        AppUtils.getInstance().showToast(this,e.getMessage());
    }


    @OnClick({R.id.email_sign_in_button, R.id.tv_create_account,R.id.tv_forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.email_sign_in_button:
                AppUtils.getInstance().hideNativeKeyboard(LoginActivity.this);
                if (isDataValid()) {

                    //Show loader on screen
                    AppUtils.getInstance().showProgressDialog(this);

                    // Get user data from input fields
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString();

                    //Sign in user with his email and password and wait for the callback in on AuthChange listener.
                    mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this);
                }
                break;

            case R.id.tv_create_account:
                // fire intent to create account activity
                startActivityForResult(new Intent(this, SignUpActivity.class), signUpActivityRequestCode);
                break;

            case R.id.tv_forgot_password:
                // fire intent to forgot password activity
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;


        }
    }

    private boolean isDataValid() {

        if (etEmail.getText().toString().isEmpty()) {
            AppUtils.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.prompt_email));
            etEmail.requestFocus();
            return Boolean.FALSE;
        } else if (!etEmail.getText().toString().matches(Constants.EMAIL_PATTERN)) {
            AppUtils.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.prompt_invalid_email));
            etEmail.requestFocus();
            return Boolean.FALSE;
        } else if (etPassword.getText().toString().isEmpty()) {
            AppUtils.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.prompt_password));
            etPassword.requestFocus();
            return Boolean.FALSE;
        } else if (etPassword.getText().toString().length() < 8) {
            AppUtils.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.prompt_password_min));
            etPassword.requestFocus();
            return Boolean.FALSE;
        } else if (etPassword.length() > 20) {
            AppUtils.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.prompt_password_max));
            etPassword.requestFocus();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == signUpActivityRequestCode && resultCode == RESULT_OK && data!=null) {
            etEmail.setText(data.getStringExtra("email"));
            etPassword.setText(data.getStringExtra("password"));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        AppUtils.getInstance().hideKeyBoard(getCurrentFocus(), ev, LoginActivity.this);
        return super.dispatchTouchEvent(ev);
    }


}
