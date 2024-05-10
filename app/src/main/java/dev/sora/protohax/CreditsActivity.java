package dev.sora.protohax;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreditsActivity extends Activity {

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

        // Any other UI setup or logic can go here
    }

    // Any additional methods or logic can be added below
}
