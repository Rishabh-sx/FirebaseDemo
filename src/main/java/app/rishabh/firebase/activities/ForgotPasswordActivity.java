package app.rishabh.firebase.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class ForgotPasswordActivity extends BaseActivity {


    @BindView(R.id.et_email)
    EditText etEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.iv_back, R.id.email_sign_in_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.email_sign_in_button:
                if(etEmail.getText().toString().trim().isEmpty() || !AppUtils.getInstance().validate(etEmail.getText().toString().trim()))
                {
                    AppUtils.getInstance().showToast(this,getString(R.string.prompt_email));
                    break;
                }else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                AppUtils.getInstance().showToast(ForgotPasswordActivity.this,"Reset password link has been sent to your shared email address");
                                onBackPressed();
                            }else
                                AppUtils.getInstance().showToast(ForgotPasswordActivity.this,"Email not registered ");
                        }
                    });
                    break;
                }

        }
    }
}
