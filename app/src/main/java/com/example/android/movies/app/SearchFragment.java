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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class SearchFragment extends Fragment {

    ListViewAdapter lviewAdapter;
    String busqueda = "";
    String[] resultIDs = {};
    String[] titles = {};
    String[] years = {};
    Activity thisActivity;
    ListView listView;
    String ID;

    public SearchFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            busqueda = arguments.getString("search");
        }
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            //busqueda = savedInstanceState.getString("busqueda");
            titles = savedInstanceState.getStringArray("titles");
            years = savedInstanceState.getStringArray("years");
            resultIDs = savedInstanceState.getStringArray("ids");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("busqueda", busqueda);
        savedInstanceState.putStringArray("titles", titles);
        savedInstanceState.putStringArray("ids", resultIDs);
        savedInstanceState.putStringArray("years", years);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        thisActivity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_search);
        //listView.setAdapter(mSearchAdapter);

        lviewAdapter = new ListViewAdapter(this.getActivity(), titles, years);


        listView.setAdapter(lviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ID = resultIDs[position];
                FetchMovie searchMovie = new FetchMovie();
                searchMovie.execute(ID);
            }
        });
        return rootView;
    }


    private void Search() {
        FetchMovies searchTask = new FetchMovies();
        searchTask.execute(busqueda);
    }

    @Override
    public void onStart() {
        super.onStart();
        Search();
    }

    public class FetchMovies extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        private String[] getSearchData(String forecastJsonStr)
                throws JSONException {
            JSONObject busqueda = new JSONObject(forecastJsonStr);
            JSONArray Busquedas = busqueda.getJSONArray("Search");

            String[] resultStrs = new String[Busquedas.length()];
            resultIDs = new String[Busquedas.length()];
            titles = new String[Busquedas.length()];
            years = new String[Busquedas.length()];

            for (int i = 0; i < Busquedas.length(); i++) {
                JSONObject searchObject = Busquedas.getJSONObject(i);
                String Titulo = searchObject.getString("Title");
                String Año = searchObject.getString("Year");
                String ID = searchObject.getString("imdbID");
                resultStrs[i] = Titulo + " - " + Año;
                resultIDs[i] = ID;
                titles[i] = Titulo;
                years[i] = "Year: " + Año;
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String searchJsonStr = null;


            try {
                final String FORECAST_BASE_URL = "http://www.omdbapi.com/?";
                final String SEARCH = "s";
                final String ID = "i";
                final String PLOT = "plot";
                final String RETURN_TYPE = "r";
                final String TYPE = "type";
                final String format = "json";
                String[] prueba = params[0].split("\\s+");
                String busqueda = prueba[0];
                for (int i = 1; i < prueba.length; ++i) {
                    busqueda = busqueda + String.valueOf(Character.toChars(43)) + prueba[i];
                }
                String URLFINAL = FORECAST_BASE_URL + "s=" + busqueda;

                Uri builtUri = Uri.parse(URLFINAL).buildUpon()
                        .appendQueryParameter(RETURN_TYPE, format)
                        .appendQueryParameter(TYPE, "movie")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                searchJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getSearchData(searchJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                lviewAdapter = new ListViewAdapter(thisActivity, titles, years);
                listView.setAdapter(lviewAdapter);
            }
        }
    }

    class Peli {
        public String Titulo;
        public String Plot;
        public String Año;
        public String Imagen;
    }

    public class FetchMovie extends AsyncTask<String, Void, Peli> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private Peli getSearchData(String forecastJsonStr)
                throws JSONException {
            JSONObject movie = new JSONObject(forecastJsonStr);
            Peli pelicula = new Peli();
            pelicula.Titulo = movie.getString("Title");
            pelicula.Año = movie.getString("Year");
            pelicula.Plot = movie.getString("Plot");
            pelicula.Imagen = movie.getString("Poster");
            return pelicula;
        }

        @Override
        protected Peli doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String searchJsonStr = null;


            try {
                final String FORECAST_BASE_URL = "http://www.omdbapi.com/?";
                final String ID = "i";
                final String PLOT = "plot";
                final String RETURN_TYPE = "r";
                final String TYPE = "type";
                final String format = "json";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(ID, params[0])
                        .appendQueryParameter(RETURN_TYPE, format)
                        .appendQueryParameter(PLOT, "full")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                searchJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getSearchData(searchJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Peli result) {
            if (result != null) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Titulo", result.Titulo)
                        .putExtra("Imagen", result.Imagen)
                        .putExtra("Plot", result.Plot)
                        .putExtra("Año", result.Año);
                startActivity(intent);
            }
        }
    }
}
