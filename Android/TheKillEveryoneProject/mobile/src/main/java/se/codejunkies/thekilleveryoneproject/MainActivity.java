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

import java.util.Timer;
import java.util.TimerTask;
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
    @InjectView(R.id.kpm)    TextView killsPerMinute;
    @InjectView(R.id.progress)    IconRoundCornerProgressBar progress;
    @InjectView(R.id.countryProgress) IconRoundCornerProgressBar countryProgress;
    int kpm = 0;

    private Timer _timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("Kills per minute","Your kpm is: " + kpm);
                kpm = 0;
            }
        },0,60*1000);

        HubConnection connection = new HubConnection("http://192.168.20.170:41498/signalr");
        final HubProxy hub = connection.createHubProxy("game");

        hub.on("updateCountry",new SubscriptionHandler1<Country>() {
            @Override
            public void run(Country _country) {
                Log.d("Kill","Got death update");
                country.setText(_country.Name);
                Log.d("Death progress","Countyr: " + _country.Name + " Current Pop:" + _country.CurrentPopulation + " Pop: " + _country.Population);
                setCountryProgress(_country.Population, _country.CurrentPopulation);
            }

        },Country.class);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kpm++;
                hub.invoke("kill");
            }
        });


        try {
            connection.start(new MyWebsocketTransport(connection.getLogger())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private void setCountryProgress(float startPopulation, float currentPopulation){
        Log.d("Update","setCountryProgress");
        Log.d("Death progress","" + Math.round((currentPopulation / startPopulation) * 100));
        countryProgress.setProgress(Math.round((currentPopulation / startPopulation) * 100));
    }

}
