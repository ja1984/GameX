package se.codejunkies.thekilleveryoneproject;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;

import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.HttpConnection;
import microsoft.aspnet.signalr.client.http.HttpConnectionFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


public class MainActivity extends ActionBarActivity {


    @InjectView(R.id.button) Button button;
    @InjectView(R.id.progress)    IconRoundCornerProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());


        String host = "http://192.168.20.170:41498/raw-connection";
        Connection connection = new Connection(host);


        try {
            connection.start(new ServerSentEventsTransport(connection.getLogger(), new HttpConnection() {
                @Override
                public HttpConnectionFuture execute(Request request, HttpConnectionFuture.ResponseCallback responseCallback) {
                    return new HttpConnectionFuture();
                }
            })).get();
        } catch (InterruptedException e) {
            Log.d("Error",e.getMessage());
            // Handle ...
        } catch (ExecutionException e) {
            Log.e("Error",e.getMessage(),e);
            // Handle ...
        }


        connection.connected(new Runnable() {
            @Override
            public void run() {
                Log.d("Connectioned","Yes");
            }
        });

        connection.closed(new Runnable() {
            @Override
            public void run() {
                Log.d("Connectioned","Disconnected");
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setProgress((progress.getProgress() + 1));
            }
        });

    }

    public void UpdateStatus( String status )
    {
        Log.d("Updatestats","" + status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
