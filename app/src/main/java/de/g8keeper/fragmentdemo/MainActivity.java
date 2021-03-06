package de.g8keeper.fragmentdemo;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public static class AuswahlFragment extends ListFragment {

        private static final String ID_LAST_SELECTION = "zuletztSelektiert";
        boolean isLandscape;
        int lastSelected = 0;

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    new String[]{"eins", "zwei", "drei", "vier"}));

            View detailsFrame = getActivity().findViewById(R.id.details);
            isLandscape = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

            if (savedInstanceState != null) {
                lastSelected = savedInstanceState.getInt(ID_LAST_SELECTION, 0);
            }




            if (isLandscape) {
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                detailsAnzeigen(lastSelected);
            }
//            Log.d(TAG, "onActivityCreated: ChoiceMode: " + getListView().getChoiceMode());
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(ID_LAST_SELECTION, lastSelected);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            detailsAnzeigen(position);

        }

        private void detailsAnzeigen(int index) {
            //Toast.makeText(getContext(), "LastSelected: " + Integer.toString(lastSelected), Toast.LENGTH_SHORT).show();

            this.lastSelected = index;


            if (isLandscape) {
                Toast.makeText(getContext(), "SetItemChecked: " + Integer.toString(index), Toast.LENGTH_SHORT).show();

                getListView().post(()-> getListView().setItemChecked(index, true));

                DetailFragment details = (DetailFragment) getFragmentManager().findFragmentById(R.id.details);

                if (details == null || details.getIndex() != index) {
                    details = DetailFragment.newInstance(index);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    fragmentTransaction.replace(R.id.details, details);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();

                }

            } else {
//                getListView().setItemChecked(index,true);

                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailFragment.INDEX, index);
                startActivity(intent);

            }

        }
    }

    public static class DetailFragment extends Fragment {

        public static final String INDEX = "index";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            ScrollView scrollView = null;

            if (container != null) {
                scrollView = new ScrollView(getActivity());
                TextView textView = new TextView(getActivity());
                scrollView.addView(textView);
                textView.setText("Element #" + (1 + getIndex()) + " ist sichtbar");
            }

            return scrollView;

        }


        public static DetailFragment newInstance(int index) {
            DetailFragment fragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putInt(INDEX, index);
            fragment.setArguments(args);
            return fragment;
        }

        public int getIndex() {
            return getArguments().getInt(INDEX, 0);
        }


    }

    public static class DetailsActivity extends AppCompatActivity {


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                finish();
                return;
            }

            if(savedInstanceState == null){
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(android.R.id.content,detailFragment).commit();
            }

        }
    }
}
