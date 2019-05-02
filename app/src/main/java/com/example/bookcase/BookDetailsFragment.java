package com.example.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class BookDetailsFragment extends Fragment {

    private TextView bookTitle, bookYear, bookAuthor;
    private ImageView bookImage;
    private ImageButton playButton, pauseButton, stopButton;
    public SeekBar audioProgress;
    private BookDetailsListener listener;
    public Button downloadButton;

    public interface BookDetailsListener {
        void bookPlay();

        void bookPause();

        void bookStop();

        void setBookPosition(int bookPosition);

        void bookDownload();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        bookTitle = view.findViewById(R.id.book_title);
        bookYear = view.findViewById(R.id.book_year);
        bookAuthor = view.findViewById(R.id.book_author);
        bookImage = view.findViewById(R.id.book_image);
        playButton = view.findViewById(R.id.play_button);
        pauseButton = view.findViewById(R.id.pause_button);
        stopButton = view.findViewById(R.id.stop_button);
        audioProgress = view.findViewById(R.id.audio_progress);
        downloadButton = view.findViewById(R.id.download_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.bookPlay();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.bookDownload();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.bookStop();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.bookPause();
            }
        });

        audioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.setBookPosition(seekBar.getProgress());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookDetailsListener) {
            listener = (BookDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Fragment Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void displayBook(Book book) {
        bookTitle.setText(book.getTitle());
        bookYear.setText(String.valueOf(book.getYearPublished()));
        bookAuthor.setText(String.valueOf(book.getAuthor()));
        setImage(book);
    }

    private void setImage(Book book) {
        String url = book.getCoverURL();

        Picasso
                .with(this.getContext())
                .load(url)
                .into(bookImage);
    }

    public void swapDownloadToDelete() {
        downloadButton.setText("Delete");
    }

    public void swapDeleteToDownload() {
        downloadButton.setText("Download");
    }
}
