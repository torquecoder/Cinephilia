package tech.torque.cinephilia.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import tech.torque.cinephilia.Adapters.PosterAdapter;
import tech.torque.cinephilia.Model.Movie;
import tech.torque.cinephilia.MoviesApi.FetchMoviesTask;
import tech.torque.cinephilia.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static public ArrayList<String> images;
    static public ArrayList<Movie> moviesList;
    static public ArrayList<String> idList;
    static public PosterAdapter posterAdapter;
    static GridView gridview;
    public static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static class MoviesFragment extends Fragment {

        public MoviesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle onSavedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setHasOptionsMenu(true);
            gridview = (GridView) rootView.findViewById(R.id.grid_view);
            int ot = getResources().getConfiguration().orientation;
            gridview.setNumColumns(ot == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
            gridview.setAdapter(posterAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("movie_id", moviesList.get(position).getId());
                    intent.putExtra("movie_position", position);
                    startActivity(intent);
                }
            });

            toast = Toast.makeText(rootView.getContext(), "", Toast.LENGTH_SHORT);
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelableArrayList("movies", MainActivity.moviesList);
            outState.putStringArrayList("images", MainActivity.images);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
                moviesList = savedInstanceState.getParcelableArrayList("movies");
                images = savedInstanceState.getStringArrayList("images");
            } else {
                moviesList = new ArrayList<>();
                images = new ArrayList<>();
                idList = new ArrayList<>();
                posterAdapter = new PosterAdapter(getActivity());
                updateMovies();
            }
            super.onCreate(savedInstanceState);
        }


        public void updateMovies() {

            FetchMoviesTask task = new FetchMoviesTask(getActivity());
            task.execute();
        }


    }
}
