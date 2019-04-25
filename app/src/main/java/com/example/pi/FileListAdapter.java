package com.example.pi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pi.entities.Message;
import com.github.abdularis.civ.AvatarImageView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Created by nahabwe on 1/18/18
 * Campus.
 * A reusable adapter class for all the list view elements
 */

public class FileListAdapter extends ArrayAdapter {

    private ArrayList<byte[]> myfiles=new ArrayList<>();
    private ArrayList<String> mytimes=new ArrayList<>();
    private ArrayList<String> mysenders=new ArrayList<>();

    @LayoutRes
    private int listHolder;
    @IdRes
    private int file;
    @IdRes
    private int time;


    /**
     * This constructor handles all instance variable declarations
     *
     */
    FileListAdapter(@LayoutRes int listHolder, @IdRes int file, @IdRes int time, @IdRes int sender, Context context, ArrayList<byte[]>files, ArrayList<String>times, ArrayList<String>senders) {
        //super(context, listHolder, in, out, tin, tout);
        super(context,listHolder,file,files);
        this.listHolder = listHolder;
        this.myfiles = files;
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
        ImageView img = ConvertView.findViewById(R.id.txtFile);
        TextView time = ConvertView.findViewById(R.id.ftime);
        TextView sender = ConvertView.findViewById(R.id.fsender);
        byte[] imgInBytes = myfiles.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
        Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.8),(int)(bitmap.getHeight()*0.8),true);
        img.setImageBitmap(bitmap);
        time.setText(mytimes.get(position));
        sender.setText(mysenders.get(position));
        return ConvertView;
    }

}
