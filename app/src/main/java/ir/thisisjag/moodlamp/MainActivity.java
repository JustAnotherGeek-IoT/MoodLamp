package ir.thisisjag.moodlamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static InetAddress receiverAddress;

    static int gate,r,g,b;
    private static int[] color={0,0,0,0};
    private PrimeThread pt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert manager != null;
        DhcpInfo info = manager.getDhcpInfo();
        gate = info.gateway;
        int[] gate1 = new int[]{(gate & 0xFF), ((gate >> 8) & 0xFF), ((gate >> 16) & 0xFF), ((gate >> 24) & 0xFF)};
        Log.d("IP in udp activity", gate1[0] +"."+ gate1[1] +"."+ gate1[2] +"."+ gate1[3]);
        byte[] ipAddr = new byte[] {(byte) gate1[0], (byte) gate1[1], (byte) gate1[2], (byte) 255 };
        try {
            receiverAddress = InetAddress.getByAddress(ipAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        pt = new PrimeThread();
        pt.start();
        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        final TextView tv = findViewById(R.id.textView);
        colorPickerView.setColorListener(new ColorEnvelopeListener() {

            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {

                    //Toast.makeText(MainActivity.this,"Color: " + Arrays.toString(envelope.getArgb()),Toast.LENGTH_SHORT ).show();
                color=envelope.getArgb();
                r=color[1];g=color[2];b=color[3];
                byte[] sendBuffer = new byte[]{(byte) 0xf2, (byte) 0xc0, 0x13, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, 0x00, 0x12, (byte) 0xc0, (byte) 0xa8,
                        (byte) 0x89, 0x69, 0x04, 0x11,(byte) g,(byte) r,(byte) b ,0x44,0x46};
                //if(color[3] < 255) {
                pt.run(receiverAddress, sendBuffer, 55667);
                    tv.setText(Arrays.toString(envelope.getArgb()) + "SENT" );
                    //pt.interrupt();
                //}
                }

        } );
    }

}
