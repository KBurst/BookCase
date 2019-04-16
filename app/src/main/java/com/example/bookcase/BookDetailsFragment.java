package com.example.bookcase;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class BookDetailsFragment extends Fragment {

    private TextView bookTitle, bookYear, bookAuthor;
    private ImageView bookImage;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        bookTitle = view.findViewById(R.id.book_title);
        bookYear = view.findViewById(R.id.book_year);
        bookAuthor = view.findViewById(R.id.book_author);
        bookImage = view.findViewById(R.id.book_image);

        return view;
    }

    public void displayBook(Book book)
    {
        bookTitle.setText(book.getTitle());
        bookYear.setText(String.valueOf(book.getYearPublished()));
        bookAuthor.setText(String.valueOf(book.getAuthor()));

        setImage(book);

    }

    private void setImage(Book book)
    {
        String url = book.getCoverURL();

        Picasso
                .with(this.getContext())
                .load(url)
                .into(bookImage);
    }


}
