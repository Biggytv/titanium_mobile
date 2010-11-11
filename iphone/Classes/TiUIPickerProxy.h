/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
#ifdef USE_TI_UIPICKER

#import "TiViewProxy.h"

// TODO: Can we split this into Picker and DatePicker classes?  It would make things a lot easier (and faster!) during view setup.
@interface TiUIPickerProxy : TiViewProxy {

@private
	NSArray* selectOnLoad;
}

-(void)setSelectedRow:(id)args;
-(void)reloadColumn:(id)column;

@end

#endif