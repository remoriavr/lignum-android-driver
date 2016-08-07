package driver.lignum.com.lignum_android_driver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.remoriavr.driver.BluetoothLeDriver;
import com.remoriavr.driver.BluetoothLeUart;

public class MainActivity extends AppCompatActivity implements BluetoothLeUart.Callback {

    //public static Context context;
    private BluetoothLeUart uart;
    private String blepacket;

    // Initialize UART.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context = this;
        uart = new BluetoothLeUart(getApplicationContext());
        Output("STATUS: onCreate Called");
    }

    // OnResume, called right before UI is displayed.  Connect to the bluetooth device.
    @Override
    protected void onResume()
    {
        super.onResume();
        uart.registerCallback(this);
        uart.connectFirstAvailable();
        Output("STATUS: onResume Called");
    }

    // OnStop, called right before the activity loses foreground focus.  Close the BTLE connection.
    @Override
    protected void onStop()
    {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        Output("STATUS: onStop Called");
    }

    // UART Callback event handlers.
    @Override
    public void onConnected(BluetoothLeUart uart)
    {
        Output("STATUS: Connected");
    }

    // Connection failed advertisement
    @Override
    public void onConnectFailed(BluetoothLeUart uart)
    {
        Output("STATUS: Connection failed");
    }

    // Advise when the UART device disconnected
    @Override
    public void onDisconnected(BluetoothLeUart uart)
    {
        Output("STATUS: Disconnected");
        uart.unregisterCallback(this);
        uart.registerCallback(this);
        uart.connectFirstAvailable();
        Output("STATUS: Reconnecting");
    }

    // Send messages to label
    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx)
    {
        blepacket += rx.getStringValue(0);
        String[] buffer = blepacket.split("\\|");
        if(buffer.length>2)
        {
            Output(buffer[1] + "|");
            blepacket = "";
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device)
    {
        Output("STATUS: Device found - " + device.getName() + "[" + device.getAddress() + "]");
    }

    @Override
    public void onDeviceInfoAvailable()
    {
        Output("STATUS: Device info available - " + uart.getDeviceInfo());
    }

    public void Output(final String message)
    {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d("state", message);
                    setContentView(R.layout.activity_main);
                    TextView t = (TextView) findViewById(R.id.LIGNUM_STATUS);
                    t.setText(message);
                }
            });
            Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
