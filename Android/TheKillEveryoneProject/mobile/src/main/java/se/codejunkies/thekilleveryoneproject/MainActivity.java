package se.codejunkies.thekilleveryoneproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import se.codejunkies.thekilleveryoneproject.Models.Country;


public class MainActivity extends ActionBarActivity {


    @InjectView(R.id.button) Button button;

    @InjectView(R.id.country)    TextView country;
    @InjectView(R.id.chart)    ColumnChartView chart;
    @InjectView(R.id.kpm)    TextView killsPerMinute;
    @InjectView(R.id.last)    TextView lastMinute;
    @InjectView(R.id.progress)    IconRoundCornerProgressBar progress;
    @InjectView(R.id.countryProgress) IconRoundCornerProgressBar countryProgress;

    private int _killsPerMinute = 0;
    private int _killsPerLastMinute = 0;

    private Timer _timer;
    private HubConnection _connection;
    private HubProxy _hub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        try {
            _connection.start(new MyWebsocketTransport(_connection.getLogger())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        int numSubcolumns = 1;
        int numColumns = 1;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue(0f, Color.WHITE));
            }
            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }
        chart.setInteractive(false);
        chart.setContainerScrollEnabled(false, ContainerScrollType.HORIZONTAL);
        chart.setZoomEnabled(false);
        chart.setColumnChartData(new ColumnChartData(columns));



        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateChart(_killsPerMinute);
                _killsPerMinute = 0;
            }
        },0,10*1000);

        _connection =  new HubConnection("http://192.168.20.170:41498/signalr");
        _hub = _connection.createHubProxy("game");

        _hub.on("updateCountry",new SubscriptionHandler1<Country>() {
            @Override
            public void run(Country _country) {
                Log.d("Kill","Got death update");
                country.setText(_country.Name);
                Log.d("Death progress","Countyr: " + _country.Name + " Current Pop:" + _country.CurrentPopulation + " Pop: " + _country.Population);
                updateProgressbar(_country.Population, _country.CurrentPopulation, countryProgress);
            }

        },Country.class);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kill();
            }
        });






    }

    private void updateChart(float value) {
        ColumnChartData data = chart.getChartData();
        List<Column> columns = data.getColumns();

        Column column = columns.get(0);

        List<SubcolumnValue> values = column.getValues() ;
        if(values.size() == 60){
            values.remove(0);
        }

        values.add(new SubcolumnValue(value, Color.WHITE));

        column.setValues(values);
        data.setColumns(columns);
        chart.setColumnChartData(data);
        lastMinute.setText("Last min: " + value);
    }

    private void kill() {
        _killsPerMinute++;
        _hub.invoke("kill");
    }

    private void updateProgressbar(float startPopulation, float currentPopulation, IconRoundCornerProgressBar countryProgress){
        Log.d("Death progress","" + Math.round((currentPopulation / startPopulation) * 100));
        countryProgress.setProgress(Math.round((currentPopulation / startPopulation) * 100));
    }

}
