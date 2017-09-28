package infnet.edu.br.assessementmonetizacao.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import infnet.edu.br.assessementmonetizacao.R;
import infnet.edu.br.assessementmonetizacao.helper.SaveOrOpenTextFile;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private Button btn_open_ad;
    private Button btn_back;
    private Button btn_delete_txt;
    private Boolean ad_closed;
    private TextView saved_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_ad));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        btn_open_ad = findViewById(R.id.btn_open_ad);
        btn_back = findViewById(R.id.btn_back);
        saved_txt = findViewById(R.id.saved_text);
        btn_delete_txt = findViewById(R.id.btn_delete_txt);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                ad_closed = true;
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(getApplicationContext(),
                                "Falha ao carregar anúncio",
                                Toast.LENGTH_LONG)
                                .show();
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                displayIntetialAd();
                super.onAdLoaded();
            }
        });

        btn_open_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ad_closed) {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }
        }); // End btn_open_ad

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
            }
        }); // End btn_back

        // opening saved text
        final SaveOrOpenTextFile saveOrOpenTextFile = new SaveOrOpenTextFile();
        saved_txt.setText(saveOrOpenTextFile.openTxt(getApplicationContext()));

        btn_delete_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveOrOpenTextFile saveOrOpenTextFile1 = new SaveOrOpenTextFile();
                saveOrOpenTextFile.deleteTxt(getApplicationContext());
                saveOrOpenTextFile.openTxt(getApplicationContext());
            }
        }); // End btn_delete_txt

    } // End onCreate

    public void displayIntetialAd(){
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }
}
