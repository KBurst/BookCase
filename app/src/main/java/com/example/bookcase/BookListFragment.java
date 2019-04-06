package com.example.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class BookListFragment extends Fragment {
    private BookListListener listener;
    private ArrayList<String> bookNames = new ArrayList<>();
    public static ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Book> allBooks = new ArrayList<>();
    private ListView bookList;
    private TextView bookDetail;
    private EditText bookSearch;
    public static String searchString = "";
    private Button searchButton;

    public interface BookListListener
    {
        void onInputSent(Book input) throws IOException;
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
        bookSearch = view.findViewById(R.id.text_query);
        searchButton = view.findViewById(R.id.search_button);


        try {
            loadBooks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BookAdapter bookAdapter = new BookAdapter(view.getContext(), bookNames);
        bookList.setAdapter(bookAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    listener.onInputSent(books.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadBooks();
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

    private void reloadBooks()
    {
        searchString = bookSearch.getText().toString();
        ArrayList<Book> newBookArray = new ArrayList<>();
        ArrayList<String> newBookNames = new ArrayList<>();

        for(int i = 0; i < allBooks.size(); i++)
        {
            if(allBooks.get(i).getTitle().contains(searchString)) {
                newBookArray.add(allBooks.get(i));
                newBookNames.add(allBooks.get(i).getTitle());
            }
        }
        books = newBookArray;
        bookNames = newBookNames;

        BookAdapter bookAdapter = new BookAdapter(getContext(), bookNames);
        bookList.setAdapter(bookAdapter);

    }

    private void loadBooks() throws InterruptedException {
        //TODO: Learn to parse JSONObject for this project.
        BookRetrieve process = new BookRetrieve();
        process.execute();
        Thread.sleep(500);

        for(int i = 0; i < books.size(); i++)
        {
            bookNames.add(books.get(i).getTitle());
            allBooks.add(books.get(i));
        }
    }

}
