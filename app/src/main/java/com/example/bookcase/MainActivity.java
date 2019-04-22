package com.example.bookcase;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListListener,
        BookDetailsFragment.BookDetailsListener {

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private Intent bookIntent;
    private boolean isServiceRunning = false;
    private boolean isConnected;
    public static boolean isBookPlaying;
    private ComponentName bookServiceName;
    private AudiobookService audioBookService;
    private int bookPosition;
    private int bookProgressCounter = 0;
    private boolean isBookMoved;
    private Book currentBook;

    private AudiobookService.MediaControlBinder bookServiceBinder;
    public ServiceConnection bookServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookServiceBinder = (AudiobookService.MediaControlBinder) iBinder;
            isConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bookServiceBinder = null;
            isConnected = false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        Intent bIntent = new Intent(this, AudiobookService.class);
        bindService(bIntent, bookServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(bookServiceConnection);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookIntent = new Intent(this, AudiobookService.class);
        audioBookService = new AudiobookService();
        bookServiceBinder = (AudiobookService.MediaControlBinder) audioBookService.onBind(bookIntent);


        bookListFragment = new BookListFragment();
        bookDetailsFragment = new BookDetailsFragment();

        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();


        if (rotation == Surface.ROTATION_0) {
            mPager = findViewById(R.id.pager);
            pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(pagerAdapter);
        } else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            final TextView textView = findViewById(R.id.book_title);
            final ListView listView = findViewById(R.id.book_list);
            final BookAdapter bookAdapter = (BookAdapter) listView.getAdapter();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    textView.setText(bookAdapter.getItem(i).toString());
                    currentBook = (Book) bookAdapter.getItem(i);
                }
            });
        }
    }
    Handler bookHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            bookProgressCounter++;
            bookPosition = (int)(100 * bookProgressCounter / ((float) currentBook.getDuration()));
            System.out.println(bookPosition);

            bookDetailsFragment.audioProgress.setProgress(bookPosition);


            return false;
        }
    });

    @Override
    public void onBackPressed() {
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
    public void onInputSent(Book input) throws IOException {
        bookDetailsFragment.displayBook(input);
        currentBook = input;
    }

    @Override
    public void bookPlay() throws InterruptedException {
        if (!isServiceRunning) {
            bindService(bookIntent, bookServiceConnection, Context.BIND_AUTO_CREATE);
            startService(bookIntent);

            bookServiceBinder.play(currentBook.getId());

            bookServiceBinder.setProgressHandler(bookHandler);
            isBookPlaying = true;
            isServiceRunning = true;
        }
        if(!isBookPlaying)
        {
            if(!isBookMoved) {
                bookServiceBinder.pause();
            }
            else
            {
                Thread.sleep(1000);
                bookServiceBinder.play(currentBook.getId(), bookPosition);
            }
            isBookPlaying = true;
        }
    }

    @Override
    public void bookPause() {
        if(isBookPlaying)
        {
            bookServiceBinder.pause();
            isBookPlaying = false;
        }
    }

    @Override
    public void bookStop() {
        if (isServiceRunning) {
            bookServiceBinder.stop();
            bookServiceBinder.setProgressHandler(null);
            bookPosition = 0;
            bookProgressCounter = 0;
            isServiceRunning = false;
            isBookPlaying = false;
        }
    }

    @Override
    public int returnBookProgress()
    {
        return 0;
    }

    @Override
    public void setBookPosition(int bookPosition)
    {
        this.bookPosition = (int)(bookPosition / 100.0 * currentBook.getDuration());
        this.bookProgressCounter = bookPosition * currentBook.getDuration() / 100;
        this.isBookMoved = true;

//        if(isBookPlaying)
//        {
//            bookServiceBinder.pause();
//            bookServiceBinder.play(currentBook.getId(), this.bookPosition);
//        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return bookListFragment;
            else
                return bookDetailsFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
