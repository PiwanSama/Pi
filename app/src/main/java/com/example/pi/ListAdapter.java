package com.example.pi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;

import java.util.ArrayList;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Created by nahabwe on 1/18/18
 * Campus.
 * A reusable adapter class for all the list view elements
 */

public class ListAdapter extends ArrayAdapter {

    private ArrayList<String> peerAvatars=new ArrayList<>();
    private ArrayList<String>peerNames=new ArrayList<>();
    private ArrayList<String> peerStatus = new ArrayList<>();
    private ArrayList<String> direction = new ArrayList<>();

    @LayoutRes
    private int listHolder;
    @IdRes
    private int avatar;
    @IdRes
    private int username;
    @IdRes
    private int online;
    @IdRes
    private int offline;


    /**
     * This constructor handles all instance variable declarations
     *
     */
    ListAdapter(@LayoutRes int listHolder, @IdRes int avatar, @IdRes int pname, @IdRes int on, @IdRes int off, Context context, ArrayList<String>avatars, ArrayList<String>names, ArrayList<String>status, ArrayList<String>direction ) {
        super(context, listHolder, pname, names);
        this.listHolder = listHolder;
        this.avatar = avatar;
        this.username = pname;
        this.online = on;
        this.offline = off;
        this.peerNames = names;
        this.peerAvatars = avatars;
        this.peerStatus=status;
    }

    /**
     * Overridden method that returns a row of the list view
     *
     * @param position    An iterating variable for the supplied arrays
     * @param ConvertView A view variable that holds the list row after is is inflated
     * @param parent      The views parent
     * @return After the items of convertView have been populated, the row is returned to the caller
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, View ConvertView, @NonNull ViewGroup parent) throws NullPointerException {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConvertView = inflater.inflate(listHolder, parent, false);
        AvatarImageView myAvatar = ConvertView.findViewById(avatar);
        TextView myName = ConvertView.findViewById(username);
        ImageView onl = ConvertView.findViewById(online);
        ImageView offl = ConvertView.findViewById(offline);
        String check = peerStatus.get(position);
        myAvatar.setInitial(peerAvatars.get(position));
        myName.setText(peerNames.get(position));
        if (check.equals("on")) {
            onl.setVisibility(View.INVISIBLE);
        } else {
            offl.setVisibility(View.INVISIBLE);
        }
        return ConvertView;
    }

}
