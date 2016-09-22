package tech.torque.cinephilia.MoviesApi;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import tech.torque.cinephilia.Activities.MainActivity;

public class FetchTrailersTask extends AsyncTask<String, Void, String> {

    int index;
    String Id;
    FetchTrailersTask(int index,String Id) {

        this.index = index;
        this.Id = Id;
    }

    @Override
    protected String doInBackground(String... params) {

        String TRAILER_URL = "http://api.themoviedb.org/3/movie/";
        TRAILER_URL += Id;
        TRAILER_URL += "/videos";
        Uri trailerUri = Uri.parse(TRAILER_URL).buildUpon()
                .appendQueryParameter("api_key", API.API_KEY)
                .build();

        String trailer_response;
        try {
            trailer_response  = getJSON(trailerUri);
            return trailer_response;

        }catch (Exception e){
            return null;
        }

    }


    @Override
    protected void onPostExecute(String response) {
        try {
            if(response != null) {

                JSONObject trailersObject = new JSONObject(response);
                JSONArray trailersArray = trailersObject.getJSONArray("results");
                String trailer_path="", trailer_name="";

                for (int i = 0; i < trailersArray.length(); i++) {

                    JSONObject trailer = trailersArray.getJSONObject(i);
                    trailer_path += trailer.getString("key");
                    trailer_path += "$";

                    trailer_name += trailer.getString("name");
                    trailer_name += "$";

                }
                MainActivity.moviesList.get(index).setTrailer_path(trailer_path);
                MainActivity.moviesList.get(index).setTrailer_name(trailer_name);
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }


    }

    public static String getJSON(Uri builtUri)
    {
        InputStream inputStream;
        StringBuffer buffer;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJson = null;

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            inputStream = urlConnection.getInputStream();
            buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJson = buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }

        return moviesJson;
    }
}
