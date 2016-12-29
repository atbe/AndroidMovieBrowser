package com.atbe.abe.topmovieslist.Activites;

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

import com.atbe.abe.topmovieslist.MovieListFragment;
import com.atbe.abe.topmovieslist.R;

import java.util.ArrayList;

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
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onExitOptionClicked(MenuItem item) {
        finish();
    }

    public void onRefreshOptionClicked(MenuItem item) {

    }

    private final String MOVIE_FRAGMENT_TAG_PREFIX = "movie_fragment_";

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

            return MovieListFragment.newInstance(position);
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
                    return "Top Rated";
            }
            return null;
        }
    }

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
