package com.example.mygallery.Statusaver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.fragments.RecentImages;
import com.example.mygallery.Statusaver.fragments.RecentVideos;
import com.example.mygallery.Statusaver.fragments.downloadImages;
import com.example.mygallery.Statusaver.fragments.downloadvideo;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(" Download Status ");
        setSupportActionBar(myToolbar);
        myToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        myToolbar.inflateMenu(R.menu.toolbar_menu);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);


        viewPager =  findViewById(R.id.pagerdiet);
        setupViewPager(viewPager);
        tabLayout =  findViewById(R.id.tab_layoutdiet);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.saver_custom_tab, null);
        tabOne.setText("Image");
        tabLayout.getTabAt(0).setCustomView(tabOne);
        TextView tabtwo = (TextView) LayoutInflater.from(this).inflate(R.layout.saver_custom_tab, null);
        tabtwo.setText("Video");
        tabLayout.getTabAt(1).setCustomView(tabtwo);
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.splash_text));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());

        adapter.addFragment(new downloadImages(), "Images");
        adapter.addFragment(new downloadvideo(), "Videos");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return this.mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.mFragmentTitleList.get(position);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}