package com.example.bookcase;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListListener,
        BookDetailsFragment.BookDetailsListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static final int NUM_PAGES = 2;

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private Intent bookIntent;
    private boolean isServiceRunning = false;
    public static boolean isBookPlaying;
    private AudiobookService audioBookService;
    private Book currentBook;
    private DownloadManager bookFileManager;
    private ArrayList<Book> downloadedBooks = new ArrayList<>();

    private File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private File bookFile;

    private AudiobookService.MediaControlBinder bookServiceBinder;
    public ServiceConnection bookServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookServiceBinder = (AudiobookService.MediaControlBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bookServiceBinder = null;
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
                    bookFile = new File(dir, currentBook.getTitle());
                }
            });
        }
    }

    Handler bookHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            bookDetailsFragment.audioProgress.setProgress(message.what);
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
    public void onInputSent(Book input) {
        bookDetailsFragment.displayBook(input);
        currentBook = input;
        if (downloadedBooks.contains(currentBook)) {
            bookDetailsFragment.swapDownloadToDelete();
        } else {
            bookDetailsFragment.swapDeleteToDownload();
        }
        bookFile = new File(dir, currentBook.getTitle());
    }

    @Override
    public void bookPlay() {
        if (currentBook == null) {
            return;
        }
        if (bookFile != null && !isServiceRunning) {
            bindService(bookIntent, bookServiceConnection, Context.BIND_AUTO_CREATE);
            startService(bookIntent);
            bookServiceBinder.play(bookFile);
            bookServiceBinder.setProgressHandler(bookHandler);
            isBookPlaying = true;
            isServiceRunning = true;
            Toast.makeText(this, "Playing from file.", Toast.LENGTH_SHORT).show();
        }
        if (!isServiceRunning) {
            bindService(bookIntent, bookServiceConnection, Context.BIND_AUTO_CREATE);
            startService(bookIntent);
            bookServiceBinder.play(currentBook.getId());
            bookServiceBinder.setProgressHandler(bookHandler);
            isBookPlaying = true;
            isServiceRunning = true;
            Toast.makeText(this, "Streaming.", Toast.LENGTH_SHORT).show();
        }
        if (!isBookPlaying) {
            bookServiceBinder.pause();
            isBookPlaying = true;
        }
    }

    @Override
    public void bookPause() {
        if (currentBook == null) {
            return;
        }
        if (isBookPlaying) {
            bookServiceBinder.pause();
            isBookPlaying = false;
        }
    }

    @Override
    public void bookStop() {
        if (currentBook == null) {
            return;
        }
        if (bookServiceBinder.isBinderAlive()) {
            bookServiceBinder.stop();
            bookServiceBinder.setProgressHandler(null);
            bookDetailsFragment.audioProgress.setProgress(0);
            isBookPlaying = false;
        }
    }

    @Override
    public void bookDownload() {
        if (currentBook == null) {
            return;
        }
        if (downloadedBooks.contains(currentBook)) {
            bookFile.delete();
            downloadedBooks.remove(currentBook);
            bookDetailsFragment.swapDeleteToDownload();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);
            } else {
                downloadProcess();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadProcess();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void downloadProcess() {
        String bookString = "https://kamorris.com/lab/audlib/download.php?id=" + currentBook.getId();

        DownloadManager.Request bookRequest = new DownloadManager.Request(Uri.parse(bookString));
        bookRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        bookRequest.allowScanningByMediaScanner();
        bookRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        bookRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentBook.getTitle());

        bookFileManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        bookFileManager.enqueue(bookRequest);

        try {
            Thread.sleep(currentBook.getDuration() * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        downloadedBooks.add(currentBook);
        bookDetailsFragment.swapDownloadToDelete();
    }

    @Override
    public void setBookPosition(int bookPosition) {
        bookServiceBinder.seekTo(bookPosition);
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
