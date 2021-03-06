package com.mobilegenomics.f5n.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mobilegenomics.f5n.R;

public class HelpActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vertical);

        linearLayout = findViewById(R.id.vertical_linear_layout);

        TextView txtIntro1 = new TextView(this);
        txtIntro1.setText(
                "F5N is a Mobile application for Reference Guided Sequence Alignment using Oxford Nanopore Technology Data\n");
        txtIntro1.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(txtIntro1);

        TextView txtIntro2 = new TextView(this);
        txtIntro2.setText(
                "Please read the following terms before you use this application\n");
        linearLayout.addView(txtIntro2);

        TextView txtIntro3 = new TextView(this);
        txtIntro3.setText(
                "This app needs <b>storage permission</b> in order to read/write files generated from the pipeline steps\n");
        linearLayout.addView(txtIntro3);

        TextView txtIntro4 = new TextView(this);
        txtIntro4.setText(Html.fromHtml("This app needs at least <b>2GB free RAM</b> to smoothly function<br>"));
        linearLayout.addView(txtIntro4);

        TextView txtIntro5 = new TextView(this);
        txtIntro5.setText(Html.fromHtml(
                "If you have less memory available, minimizing the app while running some process, may stop the "
                        + "process and generate incomplete results<br>"));
        linearLayout.addView(txtIntro5);

        TextView txtIntro6 = new TextView(this);
        txtIntro6.setText(
                Html.fromHtml("Refer <b>f5n.log</b> in main_storage/mobile-genomics folder to read the log.<br>"
                        + "Refer <b>tmp.log</b> in main_storage/mobile-genomics folder in cases where the app fails in the middle of a process.<br>"));
        linearLayout.addView(txtIntro6);

        Button btnTutorial = new Button(this);
        btnTutorial.setText("View Tutorial");
        btnTutorial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(HelpActivity.this, TutorialActivity.class));
            }
        });
        linearLayout.addView(btnTutorial);

        TextView txtContribute = new TextView(this);
        txtContribute.setText(
                "\nPlease contribute to our work by testing,debugging, developing and submitting issues on our product\n");
        linearLayout.addView(txtContribute);

        Button btnReport = new Button(this);
        btnReport.setText("Report Bugs");
        btnReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Uri web = Uri
                        .parse("https://github.com/SanojPunchihewa/f5n/issues/new?assignees=&labels=bug&template=bug_report.md&title=");
                Intent website = new Intent(Intent.ACTION_VIEW, web);
                startActivity(website);
            }
        });
        linearLayout.addView(btnReport);

        Button btnRequestFeature = new Button(this);
        btnRequestFeature.setText("Request Feature");
        btnRequestFeature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Uri web = Uri
                        .parse("https://github.com/SanojPunchihewa/f5n/issues/new?assignees=&labels=Feature+Request&template=feature_request.md&title=");
                Intent website = new Intent(Intent.ACTION_VIEW, web);
                startActivity(website);
            }
        });
        linearLayout.addView(btnRequestFeature);
    }
}
