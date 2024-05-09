package ocsf;

/**
 * This interface implements the abstract method used to display
 * objects onto the client or server UIs.
 *
 * @author Dr Robert Lagani&egrave;re
 * @author Dr Timothy C. Lethbridge
 * @version July 2000
 */
public interface ChatIF 
{
  /**
   * Method that when overriden is used to display objects onto
   * a UI.
   * 
   * @param message The message to display
   */
  public abstract void display(String message);
}