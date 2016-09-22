package tech.torque.cinephilia.MoviesApi;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import tech.torque.cinephilia.Activities.MainActivity;
import tech.torque.cinephilia.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FetchMoviesTask extends AsyncTask<Void, Void, String> {

    ProgressDialog progDialog;
    private Context mContext;

    public FetchMoviesTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog = new ProgressDialog(this.mContext);
        progDialog.setMessage("Fetching Data...");
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currDate = format1.format(cal.getTime());

        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, -31);
        String prevDate = format1.format(cal1.getTime());


        Uri builtUri = Uri.parse(API.API_URL).buildUpon()
                .appendQueryParameter("primary_release_date.gte", prevDate)
                .appendQueryParameter("primary_release_date.lte", currDate)
                .appendQueryParameter("api_key", API.API_KEY)
                .build();

        String response;
        try {
            response = getJSON(builtUri);
            return response;
        } catch (Exception e) {
            MainActivity.toast.setText("Connection Error");
            MainActivity.toast.setDuration(Toast.LENGTH_SHORT);
            MainActivity.toast.show();
            return null;
        }


    }


    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            loadInfo(response);
            progDialog.dismiss();
        } else {
            progDialog.dismiss();
            MainActivity.toast.setText("No Internet Connection");
            MainActivity.toast.setDuration(Toast.LENGTH_SHORT);
            MainActivity.toast.show();
        }

    }


    public static void loadInfo(String jsonString) {
        MainActivity.images.clear();
        MainActivity.moviesList.clear();

        try {
            if (jsonString != null) {
                JSONObject moviesObject = new JSONObject(jsonString);
                JSONArray moviesArray = moviesObject.getJSONArray("results");


                for (int i = 0; i < moviesArray.length(); i++) {

                    JSONObject movie = moviesArray.getJSONObject(i);
                    Movie movieItem = new Movie();
                    String Id = movie.getString("id");
                    movieItem.setId(Id);
                    movieItem.setOriginal_title(movie.getString("original_title"));
                    if (movie.getString("overview") == "null") {
                        movieItem.setOverview("No Overview was Found");
                    } else {
                        movieItem.setOverview(movie.getString("overview"));
                    }
                    if (movie.getString("release_date") == "null") {
                        movieItem.setRelease_date("Unknown Release Date");
                    } else {
                        movieItem.setRelease_date(movie.getString("release_date"));
                    }
                    movieItem.setVote_average(movie.getString("vote_average"));
                    movieItem.setPoster_path(movie.getString("poster_path"));
                    if (movie.getString("poster_path") == "null") {
                        MainActivity.images.add(API.IMAGE_NOT_FOUND);
                        movieItem.setPoster_path(API.IMAGE_NOT_FOUND);
                    } else {
                        MainActivity.images.add(API.IMAGE_URL + API.IMAGE_SIZE_300 + movie.getString("poster_path"));
                    }


                    MainActivity.moviesList.add(movieItem);
                    MainActivity.posterAdapter.notifyDataSetChanged();
                    new FetchTrailersTask(i, Id).execute();

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getJSON(Uri builtUri) {
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