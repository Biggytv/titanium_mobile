/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.ui.widget;

import org.appcelerator.titanium.TiDict;
import org.appcelerator.titanium.TiProxy;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.Log;
import org.appcelerator.titanium.util.TiConfig;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class TiUISpinner extends TiUIView implements OnItemSelectedListener
{
	private static final String LCAT = "TiUITest";
	private static final boolean DBG = TiConfig.LOGD;
	
	protected Spinner s;
	
	public TiUISpinner(TiViewProxy proxy) {
		super(proxy);
		if (DBG) {
			Log.d(LCAT, "Creating a text text");
		}
		//TextView tv = new TextView(getProxy().getContext());
		s = new Spinner(getProxy().getContext());
		s.setOnItemSelectedListener(this);
		setNativeView(s);
	}

	@Override
	public void processProperties(TiDict d)
	{
		super.processProperties(d);

		Spinner tv = (Spinner) getNativeView();
		if (d.containsKey("options")) {
			String[] optionText = d.getStringArray("options");
			Log.d(LCAT,"Processing options");
			//processOptions(optionText);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		            getProxy().getContext(), android.R.layout.simple_spinner_item, optionText);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    
			tv.setAdapter(adapter);
		    
		}
		if (d.containsKey("prompt")) {
			String prompt = d.getString("prompt");
			tv.setPrompt(prompt);
		}
		tv.invalidate();
	}

	private void processOptions(String[] optionText) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	            getProxy().getContext(), android.R.layout.simple_spinner_dropdown_item,
	            new String[] { "Apple", "Peach", "Banana" });
		
	    Spinner tv = (Spinner) getNativeView();
	    tv.setAdapter(adapter);
	}
	
	private void setAlignment(TextView tv, String textAlign) {
		if ("left".equals(textAlign)) {
			tv.setGravity(Gravity.TOP | Gravity.LEFT);
		} else if ("center".equals(textAlign)) {
			tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		} else if ("right".equals(textAlign)) {
			tv.setGravity(Gravity.TOP | Gravity.RIGHT);
		} else {
			Log.w(LCAT, "Unsupported alignment: " + textAlign);
		}
	}

	@Override
	public void propertyChanged(String key, Object oldValue, Object newValue, TiProxy proxy)
	{
		if (DBG) {
			Log.d(LCAT, "Property: " + key + " old: " + oldValue + " new: " + newValue);
		}
		TextView tv = (TextView) getNativeView();
		if (key.equals("text")) {
			tv.setText(TiConvert.toString(newValue));
			tv.requestLayout();
		} else if (key.equals("color")) {
			tv.setTextColor(TiConvert.toColor((String) newValue));
		} else if (key.equals("highlightedColor")) {
			tv.setHighlightColor(TiConvert.toColor((String) newValue));
		} else if (key.equals("textAlign")) {
			setAlignment(tv, (String) newValue);
			tv.requestLayout();
		} else if (key.equals("font")) {
			TiUIHelper.styleText(tv, (TiDict) newValue);
			tv.requestLayout();
		} else {
			super.propertyChanged(key, oldValue, newValue, proxy);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		String value = s.getSelectedItem().toString();
		TiDict data = new TiDict();
		data.put("value", value);

		proxy.internalSetDynamicValue("value", value, false);
		proxy.fireEvent("change", data);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		//proxy.internalSetDynamicValue("value", value, fireChange)
	}
}
