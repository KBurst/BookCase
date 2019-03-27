package com.example.bookcase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookListFragment extends Fragment {
    private BookListListener listener;
    private ArrayList<String> bookNames = new ArrayList<>();
    private ListView bookList;
    private TextView bookDetail;


    public interface BookListListener
    {
        void onInputSent(CharSequence input);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        bookList = view.findViewById(R.id.book_list);

        loadBooks();
        BookAdapter bookAdapter = new BookAdapter(view.getContext(), bookNames);
        bookList.setAdapter(bookAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(view.getContext(), bookNames.get(i), Toast.LENGTH_SHORT).show();
                CharSequence input = bookNames.get(i);
                listener.onInputSent(input);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof BookListListener)
        {
            listener = (BookListListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement Fragment Listener");

        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }


    private void loadBooks()
    {
        String [] books = getResources().getStringArray(R.array.books);

        for(int i = 0; i < books.length; i++)
        {
            this.bookNames.add(i, books[i]);
        }
    }

}
