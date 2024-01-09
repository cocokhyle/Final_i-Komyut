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

    TextView infoSubTitle2, toTxt;



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
        infoSubTitle2 = infoView.findViewById(R.id.infoSubTitle2);
        infoTitle.setText(marker.getTitle());
        infoSubTitle.setText(marker.getSnippet());

        title = infoTitle.getText().toString();
        subTitle = infoSubTitle.getText().toString();

        ImageView franchiseColor = infoView.findViewById(R.id.franchiseColor);
        LinearLayout franchiseColorContainer = infoView.findViewById(R.id.franchiseColorContainer);
        TextView noTerminal = infoView.findViewById(R.id.noTerminal);
        TextView noOfUnits = infoView.findViewById(R.id.noOfUnits);
        TextView franchiseColorTxt = infoView.findViewById(R.id.franchiseColorTxt);
       toTxt = infoView.findViewById(R.id.toTxt);

        LinearLayout todaDetailsContainer = infoView.findViewById(R.id.todaDetailsContainer);

        if (title.equals("BBTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0400");
            franchiseColor.setBackgroundResource(R.color.bbtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("St. Joseph 6 Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
                setVisibility();
            }else if (subTitle.equals("Cabuyao Retail Plaza Terminal")){
                infoSubTitle2.setText("St. Joseph 6 Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Brgy. Bigaa/Butong Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
                setVisibility();
            }
            else if (subTitle.equals("PNR Sub Terminal")){
                infoSubTitle2.setText("St. Joseph 6 Terminal");
                setVisibility();
            }
        }
        else if (title.equals("POSATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0120");
            franchiseColor.setBackgroundResource(R.color.red);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("PNR Cabuyao Terminal")){
                infoSubTitle2.setText("City Hall Terminal");
                setVisibility();
            }else if (subTitle.equals("City Hall Terminal")){
                infoSubTitle2.setText("PNR Cabuyao Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Savemore Terminal")){
                infoSubTitle2.setText("City Hall Terminal");
                setVisibility();
            }
            else if (subTitle.equals("DIY Cabuyao Terminal")){
                infoSubTitle2.setText("City Hall Terminal");
                setVisibility();
            }
        }
        else if (title.equals("BTATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0120");
            franchiseColor.setBackgroundResource(R.color.btatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("Cabuyao Retail Plaza Terminal")){
                infoSubTitle2.setText("Gasoline Benz Terminal");
                setVisibility();
            }else if (subTitle.equals("Cabuyao Town Plaza Brgy. Uno Terminal")){
                infoSubTitle2.setText("Gasoline Benz Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Gasoline Benz Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
                setVisibility();
            }
        }

        else if (title.equals("OSPOTODA")){
            noTerminal.setText("No. of Terminal: 1");
            noOfUnits.setText("No. of Units: 0001 - 0073");
            franchiseColor.setBackgroundResource(R.color.ospotoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("Ospital ng Cabuyao")){
                infoSubTitle2.setText("Anywhere near cabuyao ospital");
                setVisibility();
            }
        }
        else if (title.equals("CMSATODA")){
            noTerminal.setText("No. of Terminal: 1");
            noOfUnits.setText("No. of Units: 0001 - 0100");
            franchiseColor.setBackgroundResource(R.color.cmsatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("Cabuyao Retail Plaza Main Terminal Special Trip")){
                infoSubTitle2.setText("Anywhere near cabuyao ospital");
                setVisibility();
            }
        }
        else if (title.equals("MACATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0300");
            franchiseColor.setBackgroundResource(R.color.macatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);
            if (subTitle.equals("Ataw Terminal")){
                infoSubTitle2.setText("St. Joseph 7 Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Savemore Terminal")){
                infoSubTitle2.setText("St. Joseph 7 Terminal");
                setVisibility();
            }
            else if (subTitle.equals("St. Joseph 7 Terminal")){
                infoSubTitle2.setText("Savemore Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Celestine Home Terminal")){
                infoSubTitle2.setText("Savemore Terminal");
                setVisibility();
            }
        }
        else if (title.equals("BMBGTODA")){
            noTerminal.setText("No. of Terminal: 5");
            noOfUnits.setText("No. of Units: 0001 - 0936");
            franchiseColor.setBackgroundResource(R.color.bmbgtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Waltermart Terminal")){
                infoSubTitle2.setText("Main Terminal Banlic");
                setVisibility();
            }
            else if (subTitle.equals("Main Terminal Banlic")){
                infoSubTitle2.setText("Brgy. Gulod Purok 2");
                setVisibility();
            }
            else if (subTitle.equals("Mabuhay Simbahan Terminal")){
                infoSubTitle2.setText("Main Terminal Banlic");
                setVisibility();
            }
            else if (subTitle.equals("Mabuhay Phase 1 Terminal Baclaran")){
                infoSubTitle2.setText("Main Terminal Banlic");
                setVisibility();
            }
            else if (subTitle.equals("Brgy. Gulod Purok 2")){
                infoSubTitle2.setText("Main Terminal Banlic");
                setVisibility();
            }

        }
        else if (title.equals("CSVTODA")){
            noTerminal.setText("No. of Terminal: 6");
            noOfUnits.setText("No. of Units: 0001 - 0400");
            franchiseColor.setBackgroundResource(R.color.csvtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Katapatan Main Terminal")){
                infoSubTitle2.setText("South Ville Blk. 21 Terminal");
                setVisibility();
            }
            else if (subTitle.equals("South Ville Blk. 21 Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();
            }
            else if (subTitle.equals("South Ville Blk. 57 Terminal")){
                infoSubTitle2.setText("Southville Sunrise Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Southville Palengke Terminal")){
                infoSubTitle2.setText("Southville Sunrise Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Southville Sunrise Terminal")){
                infoSubTitle2.setText("Cabuyao Bayan Del Pilar Street");
                setVisibility();
            }
            else if (subTitle.equals("Cabuyao Bayan Del Pilar Street")){
                infoSubTitle2.setText("Southville Sunrise Terminal");
                setVisibility();
            }

        }
        else if (title.equals("SJV7TODA")){
            noTerminal.setText("No. of Terminal: 6");
            noOfUnits.setText("No. of Units: 0001 - 0130");
            franchiseColor.setBackgroundResource(R.color.sjv7toda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Katapatan Main Terminal")){
                infoSubTitle2.setText("Homes 1 Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Homes 1 Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Homes 1 Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();
            }
            else if (subTitle.equals("St. Joseph 7 Subdivision")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();
            }
            else if (subTitle.equals("Windfield Phase 5 Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();

            }

        }

        else if (title.equals("SJBTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0260");
            franchiseColor.setBackgroundResource(R.color.sjbtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Variety Terminal")){
                infoSubTitle2.setText("St. Joseph 6 Terminal(Gate)");
                setVisibility();

            }
            else if (subTitle.equals("St. Joseph 6 Terminal(Gate)")){
                infoSubTitle2.setText("Variety Terminal");
                setVisibility();

            }
            else if (subTitle.equals("St. Joseph 5 Terminal(Gate)")){
                infoSubTitle2.setText("Variety Terminal");
                setVisibility();

            }
            else if (subTitle.equals("PNR Sub Terminal")){
                infoSubTitle2.setText("St. Joseph 6 Terminal(Gate)");
                setVisibility();

            }

        }
        else if (title.equals("SICALATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0270");
            franchiseColor.setBackgroundResource(R.color.sicalatoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Mahogany 2 & 3 Terminal")){
                infoSubTitle2.setText("San Isidro Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Canaan Homes Terminal")){
                infoSubTitle2.setText("San Isidro Terminal");
                setVisibility();

            }
            else if (subTitle.equals("San Isidro Terminal")){
                infoSubTitle2.setText("Canaan Homes Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Centenial Plaza Terminal")){
                infoSubTitle2.setText("Mahogany 2 & 3 Terminal");
                setVisibility();

            }

        }
        else if (title.equals("PUDTODA")){
            noTerminal.setText("No. of Terminal: 4");
            noOfUnits.setText("No. of Units: 0001 - 0350");
            franchiseColor.setBackgroundResource(R.color.pudtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Brgy. Diezmo Terminal")){
                infoSubTitle2.setText("Pulo Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Lisp 1 Terminal")){
                infoSubTitle2.setText("Pulo Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Adelina Terminal")){
                infoSubTitle2.setText("Pulo Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Pulo Main Terminal")){
                infoSubTitle2.setText("Brgy. Diezmo Terminal");
                setVisibility();

            }
        }
        else if (title.equals("HVTODA")){
            noTerminal.setText("No. of Terminal: 2");
            noOfUnits.setText("No. of Units: 0001 - 0045");
            franchiseColor.setBackgroundResource(R.color.hvtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Hongkong Village(Gate Terminal)")){
                infoSubTitle2.setText("Hongkong Village(Loob Terminal)");
                setVisibility();

            }
            else if (subTitle.equals("Hongkong Village(Loob Terminal)")){
                infoSubTitle2.setText("Hongkong Village(Gate Terminal)");
                setVisibility();

            }
        }
        else if (title.equals("DOVTODA")){
            noTerminal.setText("No. of Terminal: 2");
            noOfUnits.setText("No. of Units: 0001 - 0025");
            franchiseColor.setBackgroundResource(R.color.dovtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Don Onofre (Gate Terminal)")){
                infoSubTitle2.setText("Dona Juan Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Dona Juan Terminal")){
                infoSubTitle2.setText("Don Onofre (Gate Terminal)");
                setVisibility();

            }
        }
        else if (title.equals("KATODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0110");
            franchiseColor.setBackgroundResource(R.color.katoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);


            if (subTitle.equals("Katapatan Main Terminal(Entrance)")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Katapatan Main Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal(Entrance)");
                setVisibility();

            }
            else if (subTitle.equals("PNC Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal(Entrance)");
                setVisibility();

            }
            else if (subTitle.equals("Katapatan PnC Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal(Entrance)");
                setVisibility();

            }
        }
        else if (title.equals("MCCHTODAI")){
            noTerminal.setText("No. of Terminal: 8");
            noOfUnits.setText("No. of Units: 0001 - 0350");
            franchiseColor.setBackgroundResource(R.color.mcchtoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Mabuhay Phase 4 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 1 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 2 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 7 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 5 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 3 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Phase 6 Terminal")){
                infoSubTitle2.setText("Mabuhay Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mabuhay Main Terminal")){
                infoSubTitle2.setText("Mabuhay Phase 7 Terminal");
                setVisibility();

            }

        }
        else if (title.equals("BOTODA")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0035");
            franchiseColor.setBackgroundResource(R.color.botoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Bamboo Subdivision")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Katapatan Main Terminal")){
                infoSubTitle2.setText("Bamboo Subdivision");
                setVisibility();

            }
            else if (subTitle.equals("CABS/CITECH Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal");
                setVisibility();

            }
        }
        else if (title.equals("MACOPASTR")){
            noTerminal.setText("No. of Terminal: 3");
            noOfUnits.setText("No. of Units: 0001 - 0100");
            franchiseColor.setBackgroundResource(R.color.macopastr);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Cabuyao Retail Plaza Terminal")){
                infoSubTitle2.setText("Dita Santa Rosa Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Mariquita Home Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Dita Santa Rosa Terminal")){
                infoSubTitle2.setText("Cabuyao Retail Plaza Terminal");
                setVisibility();

            }
        }
        else if (title.equals("LNSTODA")){
            noTerminal.setText("No. of Terminal: 5");
            noOfUnits.setText("No. of Units: 0001 - 0125");
            franchiseColor.setBackgroundResource(R.color.lnstoda);
            franchiseColorTxt.setText("Franchise Color: ");
            todaDetailsContainer.setVisibility(View.VISIBLE);

            if (subTitle.equals("Katapatan Main Terminal(Entrance)")){
                infoSubTitle2.setText("Lakeside Nest Subd. Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Lakeside Nest Subd. Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal(Entrance)");
                setVisibility();

            }
            else if (subTitle.equals("Katapatan Main Terminal")){
                infoSubTitle2.setText("Lakeside Nest Subd. Terminal");
                setVisibility();

            }
            else if (subTitle.equals("CABS/CITECH Terminal")){
                infoSubTitle2.setText("Lakeside Nest Subd. Terminal");
                setVisibility();

            }
            else if (subTitle.equals("Depante Terminal")){
                infoSubTitle2.setText("Katapatan Main Terminal(Entrance)");
                setVisibility();

            }
        }
        else if (title.equals("JEEPNEY")){
            todaDetailsContainer.setVisibility(View.GONE);
            if (subTitle.equals("Cabuyao Bayan")){
                infoSubTitle2.setText("Banlic Cabuyao");
                setVisibility();

            }
            else if (subTitle.equals("Banlic Cabuyao")){
                infoSubTitle2.setText("Cabuyao Bayan");
                setVisibility();

            }

        }else {
            todaDetailsContainer.setVisibility(View.GONE);
        }





        return infoView;
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    public void setVisibility(){
        infoSubTitle2.setVisibility(View.VISIBLE);
        toTxt.setVisibility(View.VISIBLE);
    }
}
