/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.TiDict;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.AsyncResult;
import org.appcelerator.titanium.view.TiUIView;

import ti.modules.titanium.ui.widget.TiUIPicker;
import android.app.Activity;
import android.os.Message;
import android.util.Log;

public class PickerProxy extends TiViewProxy 
{
	private ArrayList<PickerColumnProxy>columns = new ArrayList<PickerColumnProxy>();
	private ArrayList<Integer> preselectedRows = new ArrayList<Integer>();
	private static final String LCAT = "PickerProxy";
	
	private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;
	private static final int MSG_APPEND_VIEW_COLUMN = MSG_FIRST_ID + 100;
	private static final int MSG_SELECT_ROW = MSG_FIRST_ID + 101;
	private static final int MSG_REPLACE_MODEL = MSG_FIRST_ID + 102;
	
	public PickerProxy(TiContext tiContext, Object[] args)
	{
		super(tiContext, args);
	}
	
	@Override
	public TiUIView createView(Activity activity) 
	{
		TiUIPicker picker = new TiUIPicker(this, activity);
		if (columns != null && columns.size() > 0) {
			picker.addColumns( getColumnsAsListOfLists() );
			if (preselectedRows != null && preselectedRows.size() > 0) {
				picker.selectRows(preselectedRows);
			}
		}
		
		return picker;
	}
	
	private ArrayList<ArrayList<PickerRowProxy>> getColumnsAsListOfLists()
	{
		ArrayList<ArrayList<PickerRowProxy>> rowLists = new ArrayList<ArrayList<PickerRowProxy>>();
		if (columns != null && columns.size() > 0) {
			for (PickerColumnProxy column : columns) {
				rowLists.add(column.getRowArrayList());
			}
		}
		return rowLists;
	}

	// We need special handling because can also accept array
	@Override
	public void add(TiViewProxy child)
	{
		add((Object)child);
	}
	
	public void add(Object child) 
	{ 
		boolean firstColumnExists = (columns != null && columns.size() > 0);
		boolean columnAdded = false;
		if (child instanceof PickerRowProxy) {
			getFirstColumn(true).addRow((PickerRowProxy)child);
			columnAdded = !firstColumnExists;
		} else if (child.getClass().isArray()) {
			getFirstColumn(true).addRows((Object[]) child);
			columnAdded = !firstColumnExists;
		} else if (child instanceof PickerColumnProxy) {
			addColumn((PickerColumnProxy)child);
			columnAdded = true;
		} else {
			Log.w(LCAT, "Unexpected type not added to picker: " + child.getClass().getName());
			return;
		}
		
		if (columnAdded && peekView() != null) {
			appendViewColumn();
		}
	}
	
	private void appendViewColumn()
	{
		if (peekView() == null) {
			return;
		}
		if (getTiContext().isUIThread()) {
			handleAppendViewColumn(peekView());
			return;
		} else {
			AsyncResult result = new AsyncResult(peekView());
			Message msg = getUIHandler().obtainMessage(MSG_APPEND_VIEW_COLUMN, result);
			msg.sendToTarget();
			result.getResult();
		}
	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		if (msg.what == MSG_APPEND_VIEW_COLUMN) {
			AsyncResult result = (AsyncResult)msg.obj;
			handleAppendViewColumn( (TiUIView)result.getArg() );
			result.setResult(null);
			return true;
		} else if (msg.what == MSG_SELECT_ROW) {
			AsyncResult result = (AsyncResult)msg.obj;
			handleSelectRow( (TiDict)result.getArg() );
			result.setResult(null);
			return true;
		} else if (msg.what == MSG_REPLACE_MODEL) {
			AsyncResult result = (AsyncResult)msg.obj;
			handleReplaceViewModel();
			result.setResult(null);
			return true;
		} else {
			return super.handleMessage(msg);
		}
	}
	
	public void setSelectedRow(int column, int row, boolean animated)
	{
		TiUIView view = peekView();
		if (view == null) {
			// assign it to be selected after view creation
			if (preselectedRows == null) {
				preselectedRows = new ArrayList<Integer>();
			}
			while (preselectedRows.size() < (column + 1)) {
				preselectedRows.add(null);
			}
			if (preselectedRows.size() >= (column + 1)) {
				preselectedRows.remove(column);
			}
			preselectedRows.add(column, new Integer(row));
			return;
		}
		
		// View exists
		if (getTiContext().isUIThread()) {
			handleSelectRow(column, row, animated);			
		} else {
			TiDict dict = new TiDict();
			dict.put("column", new Integer(column));
			dict.put("row", new Integer(row));
			dict.put("animated", new Boolean(animated));
			AsyncResult result = new AsyncResult(dict);
			Message msg = getUIHandler().obtainMessage(MSG_SELECT_ROW, result);
			msg.sendToTarget();
			result.getResult();
		}
		
	}
	
	public PickerRowProxy getSelectedRow(int columnIndex)
	{
		if (peekView() == null) {
			return null;
		}
		
		return ((TiUIPicker)peekView()).getSelectedRow(columnIndex);
	}
	
	public PickerColumnProxy[] getColumns()
	{
		if (columns == null) {
			return new PickerColumnProxy[]{};
		} else {
			return columns.toArray(new PickerColumnProxy[columns.size()]);
		}
	}
	
	public void setColumns(Object[] rawcolumns)
	{
		if (rawcolumns == null || rawcolumns.length == 0) {
			this.columns = new ArrayList<PickerColumnProxy>();
		} else {
			if (!(rawcolumns[0] instanceof PickerColumnProxy)) {
				Log.w(LCAT, "Unexpected object type ignored for setColumns");
				return;
			} 
			this.columns = new ArrayList<PickerColumnProxy>();
			for (Object o : rawcolumns) {
				this.columns.add((PickerColumnProxy) o);
			}
		}
		
		if (peekView() != null) {
			if (getTiContext().isUIThread()) {
				handleReplaceViewModel();
			} else {
				AsyncResult result = new AsyncResult();
				Message msg = getUIHandler().obtainMessage(MSG_REPLACE_MODEL, result);
				msg.sendToTarget();
				result.getResult();
			}
		}
	}
	
	private void handleReplaceViewModel() 
	{
		TiUIPicker picker = (TiUIPicker) peekView();
		if (picker == null){
			return;
		}
		picker.replaceColumns(getColumnsAsListOfLists());
	}
	
	private void handleSelectRow(TiDict dict)
	{
		handleSelectRow(dict.getInt("column"), dict.getInt("row"), dict.getBoolean("animated"));
	}
	
	private void handleSelectRow(int column, int row, boolean animated)
	{
		if (peekView() == null) {
			return;
		}
		((TiUIPicker)peekView()).selectRow(column, row, animated);
	}

	private void handleAppendViewColumn(TiUIView view) 
	{
		if (view == null) {
			return;
		}
		TiUIPicker picker = (TiUIPicker) view;
		picker.addColumn(columns.get(columns.size() - 1).getRowArrayList());
	}
	
	private PickerColumnProxy getFirstColumn(boolean createIfMissing) 
	{
		PickerColumnProxy column = null;
		
		if (columns == null) {
			columns = new ArrayList<PickerColumnProxy>();
		}
		
		if (columns.size() == 0 && createIfMissing) {
			column = new PickerColumnProxy(getTiContext());
			columns.add(column);
		} else {
			column = columns.get(0);
		}
		
		return column;
	}
	
	private void addColumn(PickerColumnProxy column)
	{
		TiUIView view = peekView();
		columns.add(column);
		if (peekView() != null) {
			((TiUIPicker)view).addColumn(column.getRowArrayList());
		}
	}

}
