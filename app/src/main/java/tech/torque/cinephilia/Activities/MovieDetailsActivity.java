package tech.torque.cinephilia.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import tech.torque.cinephilia.Model.Movie;
import tech.torque.cinephilia.MoviesApi.API;
import tech.torque.cinephilia.R;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {


    public static Movie movie;
    public static Intent intent;
    public static TextView movie_title, movie_year, movie_overview, movie_rating_text;
    public static RatingBar movie_rating_star;
    public static ImageView movie_poster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    public void openYoutube(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + link));
        startActivity(intent);
    }

    public void viewTrailer(View view) {

        String trailer_link = movie.getTrailer_path();
        String trailer_name = movie.getTrailer_name();

        int count = 0;

        for (int i = 0; i < trailer_link.length(); i++) {
            if (trailer_link.charAt(i) == '$')
                count++;
        }

        if (count == 0) {
            Toast.makeText(getApplicationContext(), "No Video found :(", Toast.LENGTH_SHORT).show();
        } else {

            final String links[] = new String[count];
            CharSequence names[] = new String[count];
            String temp;
            int j = 0;
            for (int i = 0; i < count; i++) {
                temp = "";
                while (trailer_link.charAt(j) != '$') {
                    temp += trailer_link.charAt(j);
                    j++;
                }
                j++;
                links[i] = temp;
            }
            j = 0;
            for (int i = 0; i < count; i++) {
                temp = "";
                while (trailer_name.charAt(j) != '$') {
                    temp += trailer_name.charAt(j);
                    j++;
                }
                j++;
                names[i] = temp;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a Video");
            builder.setItems(names, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openYoutube(links[which]);
                }
            });
            builder.show();
        }

    }


    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
            WindowManager wm = (WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            initComponents(rootView);
            setValues(rootView);
            return rootView;
        }

        public void initComponents(View rootView) {
            movie = new Movie();
            MovieDetailsActivity.intent = getActivity().getIntent();
            int movie_position = intent.getIntExtra("movie_position", 0);
            movie = MainActivity.moviesList.get(movie_position);
            movie_title = (TextView) rootView.findViewById(R.id.movie_title);
            movie_year = (TextView) rootView.findViewById(R.id.movie_year);
            movie_rating_star = (RatingBar) rootView.findViewById(R.id.movie_rating);
            movie_rating_text = (TextView) rootView.findViewById(R.id.movie_rating_text);
            movie_poster = (ImageView) rootView.findViewById(R.id.movie_poster);
            movie_overview = (TextView) rootView.findViewById(R.id.movie_overview);
        }

        public static void setValues(View rootView) {
            Float rating = Float.parseFloat(movie.getVote_average());
            movie_title.setText(movie.getOriginal_title());
            movie_year.setText("Release Year : " + movie.getRelease_date().substring(0, 4));
            movie_title.setVisibility(View.VISIBLE);
            movie_rating_star.setRating(rating);
            movie_rating_text.setText("Rating : " + movie.getVote_average());
            movie_overview.setText(movie.getOverview());
            String movie_poster_url;
            if (movie.getPoster_path() == API.IMAGE_NOT_FOUND) {
                movie_poster_url = API.IMAGE_NOT_FOUND;
            } else {
                movie_poster_url = API.IMAGE_URL + API.IMAGE_SIZE_300 + "/" + movie.getPoster_path();
            }
            Picasso.with(rootView.getContext()).load(movie_poster_url).into(movie_poster);
            movie_poster.setVisibility(View.VISIBLE);

        }

    }
}

