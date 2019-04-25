package com.example.pi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pi.entities.Message;
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

public class MsgListAdapter extends ArrayAdapter {

    private ArrayList<String> mymessages=new ArrayList<>();
    private ArrayList<String>mytimes=new ArrayList<>();
    private ArrayList<String>mysenders=new ArrayList<>();
    private ArrayList<String>direction=new ArrayList<>();


    @LayoutRes
    private int listHolder;
    @IdRes
    private int msg;
    @IdRes
    private int time;
    @IdRes
    private int sender;


    /**
     * This constructor handles all instance variable declarations
     *
     */
    MsgListAdapter(@LayoutRes int listHolder, @IdRes int text, @IdRes int time, @IdRes int sender,Context context, ArrayList<String>messages, ArrayList<String>times, ArrayList<String> senders, ArrayList<String> direction) {

        super(context,listHolder,text,messages);
        this.listHolder = listHolder;
        this.direction=direction;
        this.mymessages = messages;
        this.mytimes = times;
        this.mysenders = senders;
    }

    /**
     * Overridden method that returns a row of the list view
     *
     * @param ConvertView A view variable that holds the list row after is is inflated
     * @param parent      The views parent
     * @return After the items of convertView have been populated, the row is returned to the caller
     */

    @NonNull

    public View getView(int position, View ConvertView, @NonNull ViewGroup parent) throws NullPointerException {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConvertView = inflater.inflate(listHolder, parent, false);
        TextView txt = ConvertView.findViewById(R.id.txtMessage);
        TextView time = ConvertView.findViewById(R.id.time);
        TextView sender = ConvertView.findViewById(R.id.sendername);
        if (direction.get(position)=="to"){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)txt.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            txt.setLayoutParams(params);

            RelativeLayout.LayoutParams paramst=(RelativeLayout.LayoutParams)time.getLayoutParams();
            paramst.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            time.setLayoutParams(paramst);

            RelativeLayout.LayoutParams paramsm=(RelativeLayout.LayoutParams)sender.getLayoutParams();
            paramsm.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sender.setLayoutParams(params);
        }
        txt.setText(mymessages.get(position));
        time.setText(mytimes.get(position));
        sender.setText(mysenders.get(position));
        return ConvertView;
    }

}
