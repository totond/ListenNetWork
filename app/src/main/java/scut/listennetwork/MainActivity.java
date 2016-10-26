package scut.listennetwork;

import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_WiFistate, tv_Network_state;
    private Button btn_check;
    private boolean isWifiConn,isMobileConn;
    private NetWorkStateReceiver netWorkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver,filter);
        System.out.println("注册");
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }

    private void init(){
        btn_check = (Button) findViewById(R.id.btn_check);
        tv_Network_state = (TextView) findViewById(R.id.tv_Network_state);
        tv_WiFistate = (TextView) findViewById(R.id.tv_WIFI_state);
        btn_check.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_check:
                //根据手机的Android API版本来决定用哪种方式获取网络状态
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    checkState_new();
                }else {
                    checkState_21();
                }
                break;
        }
    }

    //检测当前的网络状态
    //API版本21以下的时候调用的方法，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
    public void checkState_21(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //获取WIFI连接的信息
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isWifiConn = networkInfo.isConnected();
        //获取移动数据连接的信息
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isMobileConn = networkInfo.isConnected();
        tv_WiFistate.setText("Wifi是否连接:" + isWifiConn);
        tv_Network_state.setText("移动数据是否连接:" + isMobileConn);
    }

    //API版本21及以上的时候调用的方法
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkState_new(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //获取所有网络的信息，再一个一个取出来
        Network[] networks = connMgr.getAllNetworks();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < networks.length; i++){
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
        }
        tv_Network_state.setText(sb.toString());
    }

}
