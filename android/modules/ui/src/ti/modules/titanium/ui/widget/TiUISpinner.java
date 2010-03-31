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
			Log.d(LCAT, "Creating a spinner");
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

		if (d.containsKey("options")) {
			String[] optionText = d.getStringArray("options");
			Log.d(LCAT,"Processing options");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		            getProxy().getContext(), android.R.layout.simple_spinner_item, optionText);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    
			s.setAdapter(adapter);
		    
		}
		if (d.containsKey("prompt")) {
			String prompt = d.getString("prompt");
			s.setPrompt(prompt);
		}
		s.invalidate();
	}

	@Override
	public void propertyChanged(String key, Object oldValue, Object newValue, TiProxy proxy)
	{
		//TODO: implement property change handler
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String value = s.getSelectedItem().toString();
		TiDict data = new TiDict();
		data.put("value", value);

		proxy.internalSetDynamicValue("value", value, false);
		proxy.fireEvent("change", data);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		//Do nothing
	}
}
