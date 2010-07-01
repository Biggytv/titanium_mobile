/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.quitter;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.TiModule;

public class QuitterModule extends TiModule {

	public QuitterModule(TiContext context) {
		super(context);
	}
	
	public static void quitNow() {
		System.exit(0);
	}
}
