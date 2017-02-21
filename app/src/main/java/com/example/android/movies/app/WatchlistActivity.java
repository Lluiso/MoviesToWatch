/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.movies.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.movies.app.data.DBHelper;
import com.example.android.movies.app.data.MyContentProvider;

public class WatchlistActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DBFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DBFragment())
                .commit();
    }

    public static class DBFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        ListViewAdapter lviewAdapter;
        String[] titles = {};
        String[] years = {};
        ListView listView;
        private static final String[] PROJECTION = new String[] { "_id", "title","year","image","plot"};
        private SimpleCursorAdapter mCursorAdapter;

        public DBFragment() {
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHasOptionsMenu(true);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            listView = (ListView) rootView.findViewById(R.id.listview_search);

            DBHelper DB = new DBHelper(getActivity(), "movieDB", null, 1);
            Cursor c = DB.findAll();
            c.moveToFirst();

            int count = c.getCount();
            titles = new String[count];
            years = new String[count];
            for (Integer i = 0; i < count; ++i) {
                titles[i] = c.getString(1);
                years[i] = "Year: " + c.getString(2);
                c.moveToNext();
            }

            String[] dataColumns = { "title" };
            int[] viewIDs = { R.id.search_title };
            lviewAdapter = new ListViewAdapter(this.getActivity(), titles, years);

            mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.id.listview_search, c, dataColumns, viewIDs, 0);
            getLoaderManager().initLoader(0, null, this);

            listView.setAdapter(lviewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String title = titles[position];
                    DBHelper DB = new DBHelper(getActivity(), "movieDB", null, 1);
                    Cursor c = DB.findProduct(title);
                    c.moveToFirst();
                    Intent intent = new Intent(getActivity(), SavedDetailActivity.class)
                            .putExtra("Titulo", c.getString(1))
                            .putExtra("Imagen", c.getString(3))
                            .putExtra("Plot", c.getString(4))
                            .putExtra("Año", c.getString(2));
                    startActivity(intent);
                }
            });
            return rootView;
        }


        @Override
        public void onStart() {
            super.onStart();
        }

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI,
                    PROJECTION, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);

        }

        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }
    }

}