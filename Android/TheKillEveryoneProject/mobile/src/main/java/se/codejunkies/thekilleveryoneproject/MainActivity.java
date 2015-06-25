package se.codejunkies.thekilleveryoneproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import se.codejunkies.thekilleveryoneproject.Models.Country;


public class MainActivity extends ActionBarActivity {


    @InjectView(R.id.button) Button button;
    @InjectView(R.id.country)    TextView country;
    @InjectView(R.id.progress)    IconRoundCornerProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        HubConnection connection = new HubConnection("http://192.168.20.170:41498/signalr");
        final HubProxy hub = connection.createHubProxy("game");

        try {
            connection.start(new MyWebsocketTransport(connection.getLogger())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //calculatePercent(9300000, 9201000);

        hub.on("updateCountry",new SubscriptionHandler1<Country>() {
            @Override
            public void run(Country _country) {
                Log.d("Kill","Got death update");
                country.setText(_country.Name);
                progress.setMax(_country.Population);
                progress.setProgress(_country.CurrentPopulation);
            }

        },Country.class);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setProgress((progress.getProgress() + 1));
                hub.invoke("kill");
            }
        });

    }











    private void calculatePercent(float oldValue, float newValue){
        float diff = oldValue - newValue;
        Log.d("Calc","" + (newValue / oldValue) * 100);
        Log.d("Calc","" + Math.round((newValue / oldValue) * 100));
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
