package com.mosquitolabs.complementary.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by francesco on 7/28/14.
 */
public class PackageFragment extends Fragment {

    private ListView listLevels;
    private LevelsListAdapter listAdapter;


    public static PackageFragment newInstance(int packageIndex) {

        PackageFragment fragment = new PackageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("packageIndex", packageIndex);
        fragment.setArguments(bundle);

        Log.d("MENU_FRAGMENT", " newInstance");

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_menu_list, container, false);
        final int packageIndex = getArguments().getInt("packageIndex", 0);

        Log.d("PACKAGE_FRAGMENT", " onCreateView");

        listLevels = (ListView) rootView.findViewById(R.id.listLevels);
        listAdapter = new LevelsListAdapter(getActivity(), packageIndex);

        Utility.setViewWidth(listLevels, Utility.getWidth(getActivity()) * 90 / 100);
        listLevels.setAdapter(listAdapter);


        return rootView;
    }

    public void updateListItems() {
        listAdapter.notifyDataSetChanged();
    }


}
