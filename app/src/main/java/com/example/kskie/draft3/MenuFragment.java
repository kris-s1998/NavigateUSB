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
 * This class is the menu fragment which is used on every activity. It is used to navigate around the application.
 *  Provides buttons to the main activities of the app.
 *
 * Created by Kris Skierniewski on 05/03/2018.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{
    private final static String ARG_1 = "activityNumber"; //this parameter is used to let the fragment know which activity created it
    //its used to disable the button in the nav bar which navigates to the current activity, and changes the colour of the button relating to the current activity

    Button btnHome; //leads to MainActivity
    Button btnMap; //leads to MapActivity
    Button btnNavigate; //leads to NavigateActivity
    Button btnSettings; //leads to SettingsActivity

    private int activityNum; //store the unique number of the activity

    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    //called when a new instance of menuFragment is created, passing one parameter (the activity number)
    public static MenuFragment newInstance(int param1) {
        //create a new fragment
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        //and pass the parameter to it
        args.putInt(ARG_1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { //retrieve the argument (activity number)
            activityNum = getArguments().getInt(ARG_1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_menu, container, false);
        btnHome = v.findViewById(R.id.btn_home); //initialise home button
        btnHome.setOnClickListener(this); //and add a click listener
        btnMap = v.findViewById(R.id.btn_map); //initialise map button
        btnMap.setOnClickListener(this); //add click listener
        btnNavigate = v.findViewById(R.id.btn_navigate); //initialise navigate button
        btnNavigate.setOnClickListener(this); //add click listener
        btnSettings = v.findViewById(R.id.btn_settings); //initialise settings button
        btnSettings.setOnClickListener(this); //add click listener
        if(activityNum == NavigateActivity.ACTIVITY_NUM){ //if current activity is the navigate activity
            Drawable icon = getResources().getDrawable(R.drawable.navigate_active);
            btnNavigate.setTextColor(getResources().getColor(R.color.colorPrimary)); //then change the colour of the button text
            btnNavigate.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null); //change the colour of the button icon
        }else if (activityNum == MainActivity.ACTIVITY_NUM) { //if current activity is the main activity
            Drawable icon = getResources().getDrawable(R.drawable.home_active);
            btnHome.setTextColor(getResources().getColor(R.color.colorPrimary)); //then change the button text colour
            btnHome.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null); //change the button icon colour
        }else if (activityNum == MapActivity.activityNum) {  //if current activity is the map activity
            Drawable icon = getResources().getDrawable(R.drawable.map_active);
            btnMap.setTextColor(getResources().getColor(R.color.colorPrimary)); //then change the button text colour
            btnMap.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null); //change the button icon colour
        }else if (activityNum == SettingsActivity.ACTIVITY_NUM) { //it the activity is the settings activity
            Drawable icon = getResources().getDrawable(R.drawable.settings_active);
            btnSettings.setTextColor(getResources().getColor(R.color.colorPrimary)); //then change the button text colour
            btnSettings.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null); //and change the button icon colour
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
        //when any of the buttons in the fragment are clicked, check if the user is not already on the selected activity
        //by comparing the activity number.
        //if not the current activity, then start the appropriate activity
        if(view.getId() == btnHome.getId() && activityNum != MainActivity.ACTIVITY_NUM){
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnMap.getId() && activityNum != MapActivity.activityNum){
            Intent intent = new Intent(this.getContext(), MapActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnNavigate.getId() && activityNum != NavigateActivity.ACTIVITY_NUM){
            Intent intent = new Intent(this.getContext(), NavigateActivity.class);
            startActivity(intent);
        }else if(view.getId() == btnSettings.getId() && activityNum != SettingsActivity.ACTIVITY_NUM){
            Intent intent = new Intent(this.getContext(), SettingsActivity.class);
            startActivity(intent);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
