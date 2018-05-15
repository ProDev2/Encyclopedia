package com.prodev.encyclopedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.prodev.encyclopedia.container.storage.WordStorage;
import com.prodev.encyclopedia.fetcher.EncyclopediaFetcher;
import com.prodev.encyclopedia.fetcher.LanguageFetcher;
import com.prodev.encyclopedia.fragments.SourceFragment;
import com.prodev.encyclopedia.fragments.TranslationFragment;
import com.prodev.encyclopedia.fragments.WordsFragment;
import com.simplelib.SimpleActivity;

public class MainActivity extends SimpleActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static MainActivity activity;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    public MainActivity() {
        activity = this;
    }

    public static void switchToMenuItem(int id, boolean switchFragment) {
        try {
            if (activity != null) {
                MenuItem item = activity.navigationView.getMenu().findItem(id);
                item.setChecked(true);

                if (switchFragment)
                    activity.onNavigationItemSelected(item);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LanguageFetcher.init(this);
        EncyclopediaFetcher.init(this, new WordStorage());

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        setupDrawer();

        navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            if (navigationView.getMenu().size() > 0) {
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            }
        } catch (Exception e) {
        }
    }

    public void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_translate:
                if (!(getVisibleFragment() instanceof TranslationFragment)) {
                    clearBackStack();
                    switchTo(R.id.main_frame_layout, new TranslationFragment(), "translate");
                }
                break;

            case R.id.nav_words:
                if (!(getVisibleFragment() instanceof WordsFragment)) {
                    clearBackStack();
                    switchTo(R.id.main_frame_layout, new WordsFragment(), "words");
                }
                break;

            case R.id.nav_source:
                if (!(getVisibleFragment() instanceof SourceFragment)) {
                    clearBackStack();
                    switchTo(R.id.main_frame_layout, new SourceFragment(), "source");
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearBackStack() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStackImmediate();
            }
        } catch (Exception e) {
        }
    }
}
