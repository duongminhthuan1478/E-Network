package com.thuanduong.education.network.Ultil;

import java.util.Calendar;
import java.util.Date;
public class Time {
    public static long getCur()
    {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.getTime();
    }
    public static String timeRemaining(int pos, int duration)
    {
        int remaining = duration-pos,phut=60000;
        if(remaining<phut/6)
        {
            return "-00:0"+(remaining/1000);
        }
        else if(remaining<phut)
        {
            return "-00:"+(remaining/1000);
        }
        else if(remaining>=phut&&remaining<60*phut&&remaining%phut<phut/6)
        {
            return  "-0"+remaining/phut+":0"+(remaining%phut/1000);
        }
        else if(remaining>=phut&&remaining<60*phut)
        {
            return  "-0"+remaining/phut+":"+(remaining%phut/1000);
        }
        return "";
    }
    public static String timeRemaining(long input)
    {
        Date currentTime = Calendar.getInstance().getTime();
        long now = currentTime.getTime();
        if(now>input)
        {
            long spantime= now-input,phut=60000;
            if(spantime<phut)
            {
                return "just now";
            }
            else if(spantime>=phut&&spantime<60*phut)
            {
                return  (int)spantime/(phut)+" min ago";
            }
            else if(spantime>=60*phut&&spantime<24*60*phut)
            {
                return  (int)spantime/(60*phut)+" h ago";
            }
            else if(spantime>=24*60*phut&&spantime<2*24*60*phut)
            {
                return  "yesterday";
            }
            else
            {
                Date date = new Date(input);
                return "since "+android.text.format.DateFormat.format(" hh:mm:ss a dd-MM-yyyy ", date);
            }
        }else
        {
            long spantime= input-now,phut=60000;
            if(spantime<phut)
            {
                return "just now";
            }
            else if(spantime>=phut&&spantime<60*phut)
            {
                return  (int)spantime/(phut)+" min later";
            }
            else if(spantime>=60*phut&&spantime<24*60*phut)
            {
                return  (int)spantime/(60*phut)+" h later";
            }
            else if(spantime>=24*60*phut&&spantime<2*24*60*phut)
            {
                return  "tomorrow";
            }
            else
            {
                Date date = new Date(input);
                return "on "+android.text.format.DateFormat.format(" hh:mm:ss a dd-MM-yyyy ", date);
            }
        }
    }
    public static String timeToString(long input)
    {
        Date currentTime = Calendar.getInstance().getTime();
        long now = currentTime.getTime();
        Date date = new Date(input);
        return ""+android.text.format.DateFormat.format(" hh:mm:ss a dd-MM-yyyy ", date);
    }
    public static String LongtoTime(long input)
    {
        Date date = new Date(input);
        return android.text.format.DateFormat.format(" hh:mm:ss a dd-MM-yyyy ", date)+"";
    }

}
