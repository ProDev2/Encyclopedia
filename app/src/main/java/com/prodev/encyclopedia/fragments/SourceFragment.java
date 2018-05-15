package com.prodev.encyclopedia.fragments;

import android.view.View;

import com.prodev.encyclopedia.MainActivity;
import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.helper.SourceHelper;
import com.simplelib.SimpleFragment;

public class SourceFragment extends SimpleFragment {
    private SourceHelper sourceHelper;

    public SourceFragment() {
        super(R.layout.source_fragment);

        this.sourceHelper = new SourceHelper();
    }

    @Override
    public void create(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.switchToMenuItem(R.id.nav_source, false);
        } catch (Exception e) {
        }
    }
}
