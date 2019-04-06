package com.example.bookcase;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BookRetrieve extends AsyncTask<Void, Void, Void> {
    private String data = "";
    ArrayList<Book> bookList = new ArrayList<>();

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL bookURL = new URL("https://kamorris.com/lab/audlib/booksearch.php");
            HttpURLConnection jsonConnection = (HttpURLConnection) bookURL.openConnection();
            InputStream bookStream = jsonConnection.getInputStream();
            BufferedReader bookReader = new BufferedReader(new InputStreamReader(bookStream));

            String line = "";
            while (line != null) {
                line = bookReader.readLine();
                data = data + line;
            }

            JSONArray bookArray = new JSONArray(data);
            JSONObject bookData;
            int bookId;
            String bookTitle;
            String bookAuthor;
            int bookPublished;
            String bookImageURL;
            Book book;

            for(int i = 0; i < bookArray.length(); i++)
            {
                bookData = (JSONObject) bookArray.get(i);
                bookId = bookData.getInt("book_id");
                bookTitle = bookData.getString("title");
                bookAuthor = bookData.getString("author");
                bookPublished = bookData.getInt("published");
                bookImageURL = bookData.getString("cover_url");
                book = new Book(bookId, bookTitle, bookAuthor, bookPublished, bookImageURL);
                BookListFragment.books.add(book);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
