package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String ARG_1 = "activityNumber";

    Button btnHome;
    Button btnMap;
    Button btnNavigate;
    Button btnSettings;

    private int activityNum;

    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(int param1) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityNum = getArguments().getInt(ARG_1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_menu, container, false);
        btnHome = v.findViewById(R.id.btn_home);
        btnHome.setOnClickListener(this);
        btnMap = v.findViewById(R.id.btn_map);
        btnMap.setOnClickListener(this);
        btnNavigate = v.findViewById(R.id.btn_navigate);
        btnNavigate.setOnClickListener(this);
        btnSettings = v.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        if(activityNum == NavigateActivity.ACTIVITY_NUM){
            Drawable icon = getResources().getDrawable(R.drawable.navigate_active);
            btnNavigate.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnNavigate.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);
        }else if (activityNum == MainActivity.ACTIVITY_NUM) {
            Drawable icon = getResources().getDrawable(R.drawable.home_active);
            btnHome.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnHome.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);
        }else if (activityNum == MapActivity.activityNum) {
            Drawable icon = getResources().getDrawable(R.drawable.map_active);
            btnMap.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnMap.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);
        }else if (activityNum == SettingsActivity.activityNum) {
            Drawable icon = getResources().getDrawable(R.drawable.settings_active);
            btnSettings.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnSettings.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);
        }


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnHome.getId() && activityNum != MainActivity.ACTIVITY_NUM){
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnMap.getId() && activityNum != MapActivity.activityNum){
            Intent intent = new Intent(this.getContext(), MapActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnNavigate.getId() && activityNum != NavigateActivity.ACTIVITY_NUM){
            Intent intent = new Intent(this.getContext(), NavigateActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnSettings.getId() && activityNum != SettingsActivity.activityNum){
            Intent intent = new Intent(this.getContext(), SettingsActivity.class);
            startActivity(intent);
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
