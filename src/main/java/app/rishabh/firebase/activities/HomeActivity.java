package app.rishabh.firebase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.iv_toolbar_back)
    ImageView ivToolbarBack;

    @BindView(R.id.tv_logout)
    TextView tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initViews();
        tvToolbarTitle.setText("Let's Play");

    }

    /**
     * Initialize view on activity startup
     */
    private void initViews() {
        ivToolbarBack.setVisibility(View.GONE);
        tvLogout.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.tv_logout,R.id.btn_play_db, R.id.btn_play_storage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_play_db:
                startActivity(new Intent(this, RealtimeDbActivity.class));
                break;
            case R.id.btn_play_storage:
                startActivity(new Intent(this, StorageActivity.class));
                break;
            case R.id.tv_logout:
                FirebaseAuth.getInstance().signOut();
                AppUtils.getInstance().showToast(this,"Logged out !");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
        }
    }
}

