package com.example.bookcase;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListListener {

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookListFragment = new BookListFragment();
        bookDetailsFragment = new BookDetailsFragment();

        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if(rotation == Surface.ROTATION_0)
        {
            mPager = findViewById(R.id.pager);
            pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(pagerAdapter);
        }
        else if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
        {
            final TextView textView = findViewById(R.id.book_title);
            final ListView listView = findViewById(R.id.book_list);
            final BookAdapter bookAdapter = (BookAdapter) listView.getAdapter();



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    textView.setText(bookAdapter.getItem(i).toString());
                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onInputSent(CharSequence input) {
        bookDetailsFragment.displayBook(input.toString());
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if(position == 0)
                return bookListFragment;
            else
                return bookDetailsFragment;
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }
    }
}
