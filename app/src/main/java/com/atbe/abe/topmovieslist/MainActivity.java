package com.atbe.abe.topmovieslist;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("DEBUG: MainActivity - getItem position = " + position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MovieListFragment.newInstance(MovieListFragment.MovieOptions.NowPlayingMovies);
                case 1:
                    return MovieListFragment.newInstance(MovieListFragment.MovieOptions.UpcomingMovies);
                case 2:
                    return MovieListFragment.newInstance(MovieListFragment.MovieOptions.TopRatedMovies);
                default:
                    return MovieListFragment.newInstance(MovieListFragment.MovieOptions.NowPlayingMovies);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "In-Theatres Now";
                case 1:
                    return "Coming Soon";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

//
//
//    /// Used to follow the current page
//    private int CurrentMoviePage = 0;
//
//    /** Handles our movie requests and updates the listview.
//     * This task does not get
//     */
//    private class GetTopMovies extends AsyncTask<String, Void, MovieResultsPage> {
//
//        @Override
//        /** Initiates the network call and retrieves the movie list.
//         */
//        protected MovieResultsPage doInBackground(String... strings) {
//            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
//            // Grab the top rated movies
//            return api.getMovies().getTopRatedMovies("en", CurrentMoviePage++);
//        }
//
//        @Override
//        /** When we receive the movies, we need to update the data set for the adapter.
//         *
//         * @param results The movies that were returned.
//         */
//        protected void onPostExecute(MovieResultsPage results) {
//            super.onPostExecute(results);
//
//            // reset the movieNames and add to them
//            for (MovieDb movie : results) {
//                movieItems.add(movie);
//            }
//
//            // Notify the adapter of the changes
//            theAdapter.notifyDataSetChanged();
//
//            // Go get the trailers and images for each movie
//            GetMovieTrailersAndImages();
//        }
//    }


//    /** Handles our movie requests and updates the listview.
//     * This task does not get
//     */
//    private class GetMovieTrailers extends AsyncTask<MovieDb, Void, SparseArray<String>> {
//
//        @Override
//        /** Initiates the network call and retrieves the movie list.
//         */
//        protected SparseArray<String> doInBackground(MovieDb... movies) {
//            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
//            // Grab the top rated movies
//            SparseArray<String> trailerUrls = new SparseArray<String>();
//            for (MovieDb movie : movies) {
//                List<Video> videoList = api.getMovies().getVideos(movie.getId(), "en");
//                String youtubeId = "";
//                for (Video video : videoList) {
//                    if (video.getType().equals("Trailer") && video.getSite().equals("YouTube")) {
//                        youtubeId = video.getKey();
//                        break;
//                    }
//                }
//
//                // Only add the trailer urls which we found
//                if (!youtubeId.isEmpty()) {
//                    youtubeId = "https://www.youtube.com/watch?v=" + youtubeId;
//                    System.out.println("DEBUG: GetMovieTrailers get site = " + youtubeId);
//
//                    trailerUrls.append(movie.getId(), youtubeId);
//                } else {
//                    trailerUrls.append(movie.getId(), null);
//                }
//            }
//
//            return trailerUrls;
//        }
//
//        @Override
//        /** When we receive the movies, we need to update the data set for the adapter.
//         *
//         * @param results The movies that were returned.
//         */
//        protected void onPostExecute(SparseArray<String> urls) {
//            super.onPostExecute(urls);
//
//            for (int i = 0; i < urls.size(); i++) {
//                //System.out.println("DEBUG: GetMovieTrailers-onPostExecute URL Adding " + urls.valueAt(i));
//                mMovieTrailerUrls.append(urls.keyAt(i), urls.valueAt(i));
//
//                // Notify the adapter of the changes
//                theAdapter.notifyDataSetChanged();
//            }
//        }
//    }
}
