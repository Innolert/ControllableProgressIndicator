package toyberman.customprogressidicator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import toyberman.controllableprogressindicator.ControllableProgressIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ControllableProgressIndicator indicator = (ControllableProgressIndicator) findViewById(R.id.indicator);
        indicator.setSelectedPage(0);
        indicator.setSelectedPage(1);
        indicator.setDonePage(3);
        indicator.setUnSelectedPages(new int[]{1,3});
    }
}
