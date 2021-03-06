package com.darin.drisian;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.JsonReader;

import com.darin.drisian.data.AuthorData;
import com.darin.drisian.data.HeaderListData;
import com.darin.drisian.data.WallData;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Supplier extends Application {

    private ArrayList<AuthorData> sections;
    private ArrayList<ArrayList<WallData>> walls;

    public boolean getNetworkResources() {
        //download any resources needed for the voids below while the splash screen is showing
        //yes, this is thread-safe
        //no, it is not needed for the current setup since all the resources are in res/values/strings.xml

        sections = new ArrayList<>();
        walls = new ArrayList<>();

        URL url;
        try {
            url = new URL("https://darinmenezes.github.io/wallpapers.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.setLenient(true);

            reader.beginArray();
            for (int i = 0; reader.hasNext(); i++) {
                reader.beginObject();

                String sectionName = "", sectionIcon = "", sectionDescription = "", sectionUrl = "";
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.matches("name")) sectionName = reader.nextString();
                    else if (name.matches("icon")) sectionIcon = reader.nextString();
                    else if (name.matches("description")) sectionDescription = reader.nextString();
                    else if (name.matches("url")) sectionUrl = reader.nextString();
                    else if (name.matches("wallpapers")) {

                        ArrayList<WallData> sectionWalls = new ArrayList<>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();

                            String wallName = "", wallUrl = "";
                            boolean credit = false;
                            while (reader.hasNext()) {
                                String thisName = reader.nextName();
                                if (thisName.matches("name")) wallName = reader.nextString();
                                else if (thisName.matches("url")) wallUrl = reader.nextString();
                                else if (thisName.matches("credit")) credit = reader.nextBoolean();
                            }

                            sectionWalls.add(new WallData(this, wallName, wallUrl, sectionName, i, credit)); //specify context, wallpaper name, wallpaper url, author name, author id, and whether credit is required (see values/strings.xml for more info)
                            reader.endObject();
                        }
                        walls.add(i, sectionWalls);
                        reader.endArray();
                    }
                }

                sections.add(i, new AuthorData(sectionName, sectionIcon, sectionDescription, i, sectionUrl)); //specify section name, icon url, description, id, and url
                reader.endObject();
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //get a list of the different sections
    public ArrayList<AuthorData> getAuthors(Context context) {
        return sections;
    }

    //get a list of the different wallpapers
    public ArrayList<WallData> getWallpapers(Context context) {
        ArrayList<WallData> allWalls = new ArrayList<>();

        for (ArrayList<WallData> sectionWalls : walls) {
            allWalls.addAll(sectionWalls);
        }

        return allWalls;
    }

    public ArrayList<WallData> getWallpapers(Context context, int authorId) {
        return walls.get(authorId);
    }

    //additional info to put in the about section
    public ArrayList<HeaderListData> getAdditionalInfo(Context context) {
        ArrayList<HeaderListData> info = new ArrayList<>();
        info.add(new HeaderListData(null, context.getString(R.string.fornax), true, context.getString(R.string.fornax_url)));
        return info;
    }

    public AlertDialog getCreditDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        //dialog to be shown when credit is required
        return new AlertDialog.Builder(context)
                .setTitle(R.string.credit_required)
                .setMessage(R.string.credit_required_msg)
                .setPositiveButton("OK", onClickListener)
                .create();
    }

    public void downloadWallpaper(Context context, WallData data) {
        //start a download
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(data.url));
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.name + ".png");
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    public AlertDialog getDownloadedDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        //dialog to be shown upon completion of a download
        return new AlertDialog.Builder(context)
                .setTitle(R.string.download_complete)
                .setMessage(R.string.download_complete_msg)
                .setPositiveButton("View", onClickListener)
                .create();
    }

    //share a wallpaper
    public void shareWallpaper(Context context, WallData data) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, String.valueOf(Uri.parse(data.url)));
        context.startActivity(intent);
    }
}
