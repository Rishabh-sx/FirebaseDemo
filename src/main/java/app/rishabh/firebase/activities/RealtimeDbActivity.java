package app.rishabh.firebase.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RealtimeDbActivity extends BaseActivity {

    private static final String TAG = RealtimeDbActivity.class.getName() ;
    @BindView(R.id.iv_toolbar_back)
    ImageView ivToolbarBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.tv_db_data)
    TextView tvDbData;
    @BindView(R.id.et_data_field)
    EditText etDataField;
    @BindView(R.id.btn_post_db)
    Button btnPostDb;
    private DatabaseReference mDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);
        ButterKnife.bind(this);
        initViews();

        mDbReference = FirebaseDatabase.getInstance().getReference();
        addValueEventListener();
    }

    /**
     *
     */
    private void addValueEventListener() {
        mDbReference.child("real_time_db").child("data_posted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(String.class));
                tvDbData.setText(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Initialize view on activity startup
     */
    private void initViews() {
        ivToolbarBack.setVisibility(View.VISIBLE);
        tvLogout.setVisibility(View.GONE);
        tvToolbarTitle.setText("Real-time Database");
    }


    @OnClick({R.id.iv_toolbar_back, R.id.btn_post_db})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                onBackPressed();
                break;
            case R.id.btn_post_db:
                if (validateData())
                    postDataToDb(etDataField.getText().toString());
                break;
        }
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(etDataField.getText().toString())) {
            AppUtils.getInstance().showToast(this, "Data field can't be empty");
            return false;
        }
        return true;
    }


    /**
     * Method to post data to real time data base
     */
    private void postDataToDb(final String data) {
        mDbReference.child("real_time_db").child("data_posted").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {

                    tvDbData.setText(etDataField.getText().toString().trim());
                    etDataField.setText("");
                } else {
                    AppUtils.getInstance().showToast(RealtimeDbActivity.this, task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.getInstance().showToast(RealtimeDbActivity.this, e.getMessage());

            }
        });



    }
}

