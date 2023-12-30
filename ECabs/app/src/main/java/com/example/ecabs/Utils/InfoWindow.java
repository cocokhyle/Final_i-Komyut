package com.example.ecabs.Utils;

import static android.content.Context.MODE_PRIVATE;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ecabs.Fragments.Maps_Fragment;
import com.example.ecabs.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class InfoWindow implements GoogleMap.InfoWindowAdapter {
    Context context;

    String title;
    String subTitle;



    //code here to set the bottom information using marker.getTitle
    //Use If statement to identify what toda and to set the info

    public InfoWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoView = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        TextView infoTitle = infoView.findViewById(R.id.infoTitle);
        TextView infoSubTitle = infoView.findViewById(R.id.infoSubTitle);
        TextView infoSubTitle2 = infoView.findViewById(R.id.infoSubTitle2);
        infoTitle.setText(marker.getTitle());
        infoSubTitle.setText(marker.getSnippet());

        title = infoTitle.getText().toString();
        subTitle = infoSubTitle.getText().toString();

        ImageView franchiseColor = infoView.findViewById(R.id.franchiseColor);
        LinearLayout franchiseColorContainer = infoView.findViewById(R.id.franchiseColorContainer);
        TextView noTerminal = infoView.findViewById(R.id.noTerminal);
        TextView noOfUnits = infoView.findViewById(R.id.noOfUnits);
        TextView franchiseColorTxt = infoView.findViewById(R.id.franchiseColorTxt);

        LinearLayout todaDetailsContainer = infoView.findViewById(R.id.todaDetailsContainer);

        if (title.equals("BBTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0400");
            franchiseColor.setBackgroundResource(R.color.bbtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("St. Joseph Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
            }else if (subTitle.equals("Cabuyao Retail Plaza Terminal")){
                infoSubTitle2.setText("St. Joseph Terminal");
            }
        }
        else if (title.equals("BTATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0120");
            franchiseColor.setBackgroundResource(R.color.btatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("POSATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0120");
            franchiseColor.setBackgroundResource(R.color.red);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("OSPOTODA")){
            noTerminal.setText("No. of Terminal: 1");
            noOfUnits.setText("No. of Units: 0001 - 0073");
            franchiseColor.setBackgroundResource(R.color.ospotoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("CMSATODA")){
            noTerminal.setText("No. of Terminal: 1");
            noOfUnits.setText("No. of Units: 0001 - 0100");
            franchiseColor.setBackgroundResource(R.color.cmsatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("MACATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0300");
            franchiseColor.setBackgroundResource(R.color.macatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("BMBGTODA")){
            noTerminal.setText("No. of Terminal: 5");
            noOfUnits.setText("No. of Units: 0001 - 0936");
            franchiseColor.setBackgroundResource(R.color.bmbgtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("CSVTODA")){
            noTerminal.setText("No. of Terminal: 6");
            noOfUnits.setText("No. of Units: 0001 - 0400");
            franchiseColor.setBackgroundResource(R.color.csvtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("SJV7TODA")){
            noTerminal.setText("No. of Terminal: 6");
            noOfUnits.setText("No. of Units: 0001 - 0130");
            franchiseColor.setBackgroundResource(R.color.sjv7toda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("SJV7TODA")){
            noTerminal.setText("No. of Terminal: 6");
            noOfUnits.setText("No. of Units: 0001 - 0130");
            franchiseColor.setBackgroundResource(R.color.sjv7toda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("SJBTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0260");
            franchiseColor.setBackgroundResource(R.color.sjbtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("SICALATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0270");
            franchiseColor.setBackgroundResource(R.color.sicalatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("PUDTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0350");
            franchiseColor.setBackgroundResource(R.color.pudtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("HVTODA")){
            noTerminal.setText("No. of Terminal: 2");
            noOfUnits.setText("No. of Units: 0001 - 0045");
            franchiseColor.setBackgroundResource(R.color.hvtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("DOVTODA")){
            noTerminal.setText("No. of Terminal: 2");
            noOfUnits.setText("No. of Units: 0001 - 0025");
            franchiseColor.setBackgroundResource(R.color.dovtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("KATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0110");
            franchiseColor.setBackgroundResource(R.color.katoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("MCCHTODA")){
            noTerminal.setText("No. of Terminal: 8");
            noOfUnits.setText("No. of Units: 0001 - 0350");
            franchiseColor.setBackgroundResource(R.color.mcchtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("BOTODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0035");
            franchiseColor.setBackgroundResource(R.color.botoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("MACOPASTR")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0100");
            franchiseColor.setBackgroundResource(R.color.macopastr);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("LNSTODA")){
            noTerminal.setText("No. of Terminal: 5");
            noOfUnits.setText("No. of Units: 0001 - 0125");
            franchiseColor.setBackgroundResource(R.color.lnstoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
        }
        else if (title.equals("JEEPNEY")){
            todaDetailsContainer.setVisibility(View.GONE);
            infoSubTitle2.setText("Banlic Cabuyao");
        }else {
            todaDetailsContainer.setVisibility(View.GONE);
        }





        return infoView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
