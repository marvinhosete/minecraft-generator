package me.tuskdev.generator.inventory;

/**
 * Handler to catch View errors.
 *
 * @author Natan Vieira
 */
@FunctionalInterface
public interface ViewErrorHandler {

	/**
	 * Called when an error occurs in some View handler.
	 *
	 * @param context The current view context.
	 * @param exception The caught exception.
	 */
	void error(ViewContext context, Exception exception);

}
