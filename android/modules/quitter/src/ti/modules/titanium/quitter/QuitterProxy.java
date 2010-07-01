/**
 * 
 */
package ti.modules.titanium.quitter;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.TiProxy;

/**
 * @author joel
 *
 */
public class QuitterProxy extends TiProxy {

	/**
	 * @param tiContext
	 */
	public QuitterProxy(TiContext context, Object[] args) {
		super(context);
		
		QuitterModule.quitNow();
	}
}
