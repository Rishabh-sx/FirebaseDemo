package app.rishabh.firebase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.rishabh.firebase.utils.AppSharedPreference;
import app.rishabh.firebase.utils.AppUtils;
import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends BaseActivity {

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_sign_up)
    Button btnSignup;
    @BindView(R.id.tv_already_registered)
    TextView tvAlreadyRegistered;
    @BindView(R.id.relative_layout)
    RelativeLayout mLayoutParent;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
    }


    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null)
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AppUtils.getInstance().hideProgressDialog();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent();
                                intent.putExtra("email",etEmail.getText().toString().trim());
                                intent.putExtra("password",etPassword.getText().toString().trim());
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AppUtils.getInstance().hideProgressDialog();
                    AppUtils.getInstance().showToast(SignUpActivity.this,e.getMessage());
                }
            });
    }


    /**
     * Method to signUp with email and password.
     */
    private void signUpWithEmailAndPassword() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password)

        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerificationEmail();
                    AppUtils.getInstance().showToast(SignUpActivity.this,"Registered Successfully,Sent Verification Email to you");
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.getInstance().hideProgressDialog();
                AppUtils.getInstance().showToast(SignUpActivity.this,e.getMessage());
            }
        });
    }


    @OnClick({R.id.btn_sign_up, R.id.tv_already_registered, R.id.relative_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative_layout:
                AppUtils.getInstance().hideNativeKeyboard(SignUpActivity.this);
                break;
            case R.id.btn_sign_up:
                AppUtils.getInstance().hideNativeKeyboard(SignUpActivity.this);
                if (validateData()) {
                    AppUtils.getInstance().showProgressDialog(SignUpActivity.this);
                    signUpWithEmailAndPassword();
                }
                break;
            case R.id.tv_already_registered:
                onBackPressed();
                break;
        }
    }

    /**
     * Method to validate data of input field
     *
     * @return true if data is valid
     */
    private boolean validateData() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        //Email empty check
        if (TextUtils.isEmpty(email)) {
            AppUtils.getInstance().showToast(SignUpActivity.this,getResources().getString(R.string.prompt_email));
            etEmail.requestFocus();
            return Boolean.FALSE;
        }
        // Email pattern check
        else if (!email.matches(Constants.EMAIL_PATTERN)) {
            AppUtils.getInstance().showToast(SignUpActivity.this,getResources().getString(R.string.prompt_invalid_email));
            etEmail.requestFocus();
            return Boolean.FALSE;
        }
        // Password empty check
        else if (TextUtils.isEmpty(password)) {
            AppUtils.getInstance().showToast(SignUpActivity.this,getResources().getString(R.string.prompt_password));
            etPassword.requestFocus();
            return Boolean.FALSE;
            // Password min length check
        } else if (password.length() < 8) {
            AppUtils.getInstance().showToast(SignUpActivity.this,getResources().getString(R.string.prompt_password_min));
            etPassword.requestFocus();
            return Boolean.FALSE;
            // Password max length check
        } else if (password.length() > 20) {
            AppUtils.getInstance().showToast(SignUpActivity.this,getResources().getString(R.string.prompt_password_max));
            etPassword.requestFocus();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        AppUtils.getInstance().hideKeyBoard(getCurrentFocus(), ev, SignUpActivity.this);
        return super.dispatchTouchEvent(ev);
    }


}
