package com.example.fragment;

import android.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> myForecastArrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                         if (networkInfo != null) {
                             FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
                             fetchWeatherTask.execute("http://mpianatra.com/Courses/files/data.json");
                            }


        //Create a root view for the fragment
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

        String[] forecastArray = {
                "Today - Sunny - 100 / 63",
                "Tomorrow - Sunny - 100 / 46",
                "Saturday - Sunny - 100 / 63",
                "Sunday - Sunny - 100 / 51",
                "Monday - Sunny - 100 / 46",
                "Tuesday - Sunny - 100 / 68"};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        myForecastArrayAdapter = new ArrayAdapter<String>(
                //The current context
                getActivity(),
                //ID of the list item layout
                R.layout.layout_each_item,
                // ID of the textView to populate
                R.id.tv_element_list,
                //Forecast data
                weekForecast);
        //Reference to the listView
        ListView myListView = (ListView) rootView.findViewById(R.id.listView_forecast);
        //Set array adapter on the listView
        myListView.setAdapter(myForecastArrayAdapter);


        return rootView;


    }
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            String link = params[0];

            if (params.length == 0) {
                link = "http://mpianatra.com/Courses/files/data.json";
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //JSON response as a string.
            String cleanNewsJsonStr = null;


            try {

                final String BASE_URL = link;


                URL url = new URL(BASE_URL);


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
                reader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));


                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                cleanNewsJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("TAG", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            }

            String[] newsTwentyDaysArray = new String[0];
            try {
                newsTwentyDaysArray = getNewsTitlesDataFromJson(cleanNewsJsonStr, 20);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newsTwentyDaysArray;
        }



        @Override
        protected void onPostExecute(String[] newsTwentyDaysArray) {


            //This will log the address to the array object
            Log.e(LOG_TAG, "newsTwentyDaysArray: " + newsTwentyDaysArray);

            for (int i = 0; i < newsTwentyDaysArray.length; i++) {
                String everyday = newsTwentyDaysArray[i];

                Log.e(LOG_TAG, "forecastEachDay: " + everyday);

            }

            List<String> stringList = new ArrayList<String>(Arrays.asList(newsTwentyDaysArray));
            myForecastArrayAdapter.clear();
            myForecastArrayAdapter.addAll(stringList);


            super.onPostExecute(newsTwentyDaysArray);
        }


    }


    /**
     * Take the String representing the complete information in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getNewsTitlesDataFromJson(String newsJsonStr, int numNews)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "allNews";
        final String OWM_LINK = "link";
        final String OWM_TITLE = "title";

        JSONObject newsJsonObject = new JSONObject(newsJsonStr);
        JSONArray linkAndTitleArray = newsJsonObject.getJSONArray(OWM_LIST);


        String[] resultStrs = new String[numNews];
        for (int i = 0; i < linkAndTitleArray.length(); i++) {

            // Get the JSON object representing one news
            JSONObject news = linkAndTitleArray.getJSONObject(i);


            String link = (String) news.get(OWM_LINK);
            String title = (String) news.get(OWM_TITLE);

            resultStrs[i] = (i + 1) + " - " + title;
        }

        for (String s : resultStrs) {
            Log.v("LOG_TAG", "Forecast entry: " + s);
        }
        return resultStrs;

    }

}
