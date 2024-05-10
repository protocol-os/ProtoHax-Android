package dev.sora.protohax;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dev.sora.protohax.R.layout.activity_credits);

        // Set up the toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(dev.sora.protohax.R.id.creditsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the title and text content
        TextView creditsTextView = findViewById(dev.sora.protohax.R.id.creditsTextView);
        creditsTextView.setText("Made by Strike");

        // Any other UI setup or logic can go here
    }

    // Any additional methods or logic can be added below
}
