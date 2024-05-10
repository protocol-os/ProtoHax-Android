package dev.sora.protohax.forwarder.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.creditsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the title and text content
        TextView creditsTextView = findViewById(R.id.creditsTextView);
        creditsTextView.setText("Made by Strike");
    }
}
