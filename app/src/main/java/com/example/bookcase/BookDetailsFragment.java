package com.example.bookcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailsFragment extends Fragment {

    TextView bookDetail;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        bookDetail = view.findViewById(R.id.book_title);
        return view;
    }

    public void displayBook(String title)
    {
        bookDetail.setText(title);
    }


}
