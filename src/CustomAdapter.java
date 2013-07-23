package com.example.lirurssreader_v4;

import java.util.ArrayList;

import org.jsoup.Jsoup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<post> objects;
	  
	
	public CustomAdapter(Context context, ArrayList<post> objects) {
	      inflater = LayoutInflater.from(context);
	      this.objects = objects;
	   }
	
	 private class ViewHolder {
	      TextView tvTitle;
	      TextView tvDesc;
	      TextView tvLink;
	      TextView tvPubDate;
	   }
	 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return objects.size();
	}

	

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		 return position;
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder holder = null;
	      if(convertView == null) {
	         holder = new ViewHolder();
	         convertView = inflater.inflate(R.layout.item, null);
	         holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
	         holder.tvDesc = (TextView) convertView.findViewById(R.id.tvDescription);
	         holder.tvLink = (TextView) convertView.findViewById(R.id.tvLink);
	         holder.tvPubDate = (TextView) convertView.findViewById(R.id.tvPubDate);
	         convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      holder.tvTitle.setText(objects.get(position).getTitle());
	     // SpannedString Desc =new SpannedString(objects.get(position).getDescr());
	     // holder.tvDesc.setText(Html.toHtml(Desc));
	      holder.tvDesc.setText(html2text(objects.get(position).getDescr()));
	      holder.tvLink.setText(objects.get(position).getPdaUrl());
	      holder.tvPubDate.setText(objects.get(position).getpubDate());
	      return convertView;
	}
	
	@Override
	 public post getItem(int position) {
	      return objects.get(position);
	   }

	 
}
