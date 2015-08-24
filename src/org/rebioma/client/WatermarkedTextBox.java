package org.rebioma.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;

public class WatermarkedTextBox extends TextBox implements BlurHandler, FocusHandler
{
    String watermark;
    HandlerRegistration blurHandler;
    HandlerRegistration focusHandler;
     
    public WatermarkedTextBox( )
    {
        super();
        this.setStylePrimaryName("rebioma-txtinput");
    }
     
    public WatermarkedTextBox(String defaultValue)
    {
        this();
        setText(defaultValue);
    }
     
    public WatermarkedTextBox(String defaultValue, String watermark)
    {
        this(defaultValue);
        setWatermark(watermark);
    }
     
    @Override
	public String getText() {
		String text = super.getText();
		if(text != null && text.equalsIgnoreCase(watermark)){
			text = "";
		}
		return text;
	}

	/**
     * Adds a watermark if the parameter is not NULL or EMPTY
     * 
     * @param watermark
     */
    public void setWatermark(final String watermark)
    {
        this.watermark = watermark;
         
        if ((watermark != null) && (watermark != ""))
        {
            blurHandler = addBlurHandler(this);
            focusHandler = addFocusHandler(this);
            EnableWatermark();
        }
        else
        {
            // Remove handlers
            blurHandler.removeHandler();
            focusHandler.removeHandler();
        }
    }
 
    public String getWatermark() {
		return watermark;
	}

	@Override
    public void onBlur(BlurEvent event)
    {
        EnableWatermark();
    }
     
    void EnableWatermark()
    {
        String text = getText(); 
        if ((text.length() == 0) || (text.equalsIgnoreCase(watermark)))
        {
            // Show watermark
            setText(watermark);
            addStyleDependentName("watermark");
        }
    }
 
    @Override
    public void onFocus(FocusEvent event)
    {
        removeStyleDependentName("watermark");
        String text = getText();  
        if (text.length() == 0 || text.equalsIgnoreCase(watermark))
        {
            // Hide watermark
            setText("");
        }
    }
}