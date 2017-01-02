package com.atbe.abe.topmovieslist;

/**
 * Created by abe on 12/24/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    /// The recycler this fragment belongs to
    private RecyclerView mRecyclerView;

    /// The adapter the recycler view is using
    private MovieRecyclerAdapter mRecyclerviewAdapter;

    // The linear layout manager for the recycler view
    private LinearLayoutManager mLinearLayoutManager;

    /// Broadcast receiver for refresh messages
    private BroadcastReceiver mRefreshReceiver;

    private MovieOptions mMovieOption;

    // Swipe to refresh layout
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void setmMovieOption(MovieOptions mMovieOption) {
        this.mMovieOption = mMovieOption;
    }

    @Override
    public void onRefresh() {
        System.out.println("DEBUG: MovieListFragment(onRefresh) - called.");
        mSwipeRefreshLayout.setRefreshing(true);
        RefreshMoviesList();
    }

    /** These are the different options for the list of movies available. It's
     * easier to think of each of these as a button you can choose and movies
     * get spit out based on the button. (buttons == tabs in this case)
     */
    public enum MovieOptions {
        NowPlayingMovies, TopRatedMovies, UpcomingMovies;
    }

    /** Refreshing the list of movies and images if needed. Checks if an
     * internet connection is available first and notifies user with a toast
     * if no connection is available.
     */
    public void RefreshMoviesList() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Only execute the network calls if we did not have movies before
            if (mRecyclerviewAdapter.isEmpty()) {
                new GetMoviesListTask(mRecyclerviewAdapter).execute(mMovieOption);
            } else {
                // we have movies, check on the images in-case of a network mishap
                mRecyclerviewAdapter.refreshImages();
            }
        } else {
            Toast.makeText(getContext(), "No internet connection available. Please refresh.",
                    Toast.LENGTH_SHORT).show();
        }

        // disable the refresh spinner
        System.out.println("DEBUG: MovieListFragment(RefreshMovieList) - disabling refresh NOW.");
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        System.out.println("DEBUG: MovieListFragment(onActivityCreated) - called.");

        // Initializations
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerviewAdapter = new MovieRecyclerAdapter(new ArrayList<MovieContainer>(), mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize the swipe to refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_to_refresh_movie_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // once the activity is created, we should go and capture some movies
        RefreshMoviesList();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mRefreshReceiver != null) {
            getContext().unregisterReceiver(mRefreshReceiver);
            mRefreshReceiver = null;
        }
        super.onDestroy();
    }

    // broadcast message for the refresh intent
    public static final String REFRESH_MOVIE_LISTS_BROADCAST_MESSAGE = "refresh_movie_lists_broadcast";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * This receiver is called when the fragment should go and refresh
         * the list of movies. This is called when the user chooses refresh
         * in the overflow menu.
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(REFRESH_MOVIE_LISTS_BROADCAST_MESSAGE);
        mRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("DEBUG: MovieListFragment(onReceive) - called.");

                RefreshMoviesList();
            }
        };

        // last but not least, register the receiver
        getContext().registerReceiver(mRefreshReceiver, filter);

        super.onCreate(savedInstanceState);
    }

    /** This handles the creation of a fragment and packages the option of content
     * off to the
     *
     * @param optionInt The movie option selected
     * @return Newly constructed MovieListFragment.
     */
    public static MovieListFragment newInstance(int optionInt) {
        System.out.println("DEBUG: MovieListFragment(newInstance) - called.");
        MovieOptions option;
        switch(optionInt) {
            case 0:
                option = MovieOptions.NowPlayingMovies;
                break;
            case 1:
                option = MovieOptions.UpcomingMovies;
                break;
            case 2:
                option = MovieOptions.TopRatedMovies;
                break;
            default:
                option = MovieOptions.NowPlayingMovies;
        }
        System.out.println("DEBUG: MovieListFragment(newInstance) - option = " + option.toString());

        MovieListFragment fragment = new MovieListFragment();
        fragment.setmMovieOption(option);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("DEBUG: MovieListFragment(onCreateView) - called.");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    /// Used to follow the current page
    private int CurrentMoviePage = 0;

    /** This ASyncTask handles the retrieval of the MovieResultsPage
     * for the movies in this fragment.
     */
    private class GetMoviesListTask extends AsyncTask<MovieOptions, Void, MovieResultsPage> {
        // Keep a reference to the adapter in-case it gets destroyed
        WeakReference<MovieRecyclerAdapter> mAdapter;

        /** Task constructor initializes the mAdapter.
         *
         * @param adapter The adapter the task is executing for.
         */
        public GetMoviesListTask(MovieRecyclerAdapter adapter) {
            mAdapter = new WeakReference<MovieRecyclerAdapter>(adapter);
            System.out.println("DEBUG: MovieListFragment(GetMoviesListTask) - Constructor called.");
        }

        @Override
        /** Depending on the option requested, a different call will be made to TmDB
         */
        protected MovieResultsPage doInBackground(MovieOptions... options) {
            // assuming only one option was passed
            MovieOptions option = options[0];

            System.out.println("DEBUG: GetMoviesListTask(doInBackground) - Getting " + option.toString());
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
            switch (option) {
                case NowPlayingMovies:
                    return api.getMovies().getNowPlayingMovies("en", CurrentMoviePage);
                case UpcomingMovies:
                    // TODO: Fix this call to grab upcoming movies from the current date
                    return api.getMovies().getUpcoming("en", CurrentMoviePage);
                case TopRatedMovies:
                    return api.getMovies().getTopRatedMovies("en", CurrentMoviePage);
            }

            // TODO: Is there a way around this silly return?
            return null;
        }

        @Override
        /** We need to make sure results, and the adapter are valid. The fragment must also be
         * added to the adapter before we proceed with the result collection.
         */
        protected void onPostExecute(MovieResultsPage results) {
            super.onPostExecute(results);

            if (mAdapter != null && results != null && isAdded()) {
                final MovieRecyclerAdapter adapter = mAdapter.get();
                if (adapter != null) {
                    for (MovieDb movie : results) {
                        //System.out.println("DEBUG: GetMoviesListTask - Adding movie " + movie.getTitle());
                        adapter.addMovie(new MovieContainer(movie, getActivity(), mRecyclerviewAdapter));

                        // update so the changes take effect
                        mRecyclerviewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}
