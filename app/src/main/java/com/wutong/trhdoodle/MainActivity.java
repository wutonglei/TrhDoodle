package com.wutong.trhdoodle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wutong.trhdoodleview.DoodleView;
import com.wutong.trhdoodleview.WebViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.doodle)
    DoodleView doodle;
    @BindView(R.id.btn_normal)
    Button btnNormal;
    @BindView(R.id.btn_eidt)
    Button btnEidt;
    WebViewUtil myWebView;
    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.btn_allow)
    Button btnAllow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        myWebView = new WebViewUtil(this);
        ////        myWebView.setProgress(pb);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myWebView.intoView(rl, params);
        myWebView.loadUrl("https://www.baidu.com/");
    }

    @OnClick({R.id.btn_normal, R.id.btn_eidt, R.id.btn_delete, R.id.btn_allow, R.id.btn_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_normal:
                doodle.setEidtMode(false);
                break;
            case R.id.btn_eidt:
                doodle.setEidtMode(true);
                break;
            case R.id.btn_delete:
                doodle.deleteSelected();
                break;
            case R.id.btn_allow:
                doodle.changeIntercept();

                break;
            case R.id.btn_test:
                Toast.makeText(this, "摸你", Toast.LENGTH_SHORT).show();
                break;


        }
    }
}
