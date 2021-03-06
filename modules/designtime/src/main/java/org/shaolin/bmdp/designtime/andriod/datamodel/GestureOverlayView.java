//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.28 at 04:10:22 PM CST 
//


package org.shaolin.bmdp.designtime.andriod.datamodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for GestureOverlayView complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GestureOverlayView">
 *   &lt;complexContent>
 *     &lt;extension base="{}FrameLayout">
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}eventsInterceptionEnabled"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}fadeDuration"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}fadeEnabled"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}fadeOffset"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureColor"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureStrokeAngleThreshold"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureStrokeLengthThreshold"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureStrokeSquarenessThreshold"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureStrokeType"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gestureStrokeWidth"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}orientation"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}uncertainGestureColor"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GestureOverlayView")
@XmlRootElement(name = "GestureOverlayView")
public class GestureOverlayView
    extends FrameLayout
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "eventsInterceptionEnabled", namespace = "http://schemas.android.com/apk/res/android")
    protected String eventsInterceptionEnabled;
    @XmlAttribute(name = "fadeDuration", namespace = "http://schemas.android.com/apk/res/android")
    protected String fadeDuration;
    @XmlAttribute(name = "fadeEnabled", namespace = "http://schemas.android.com/apk/res/android")
    protected String fadeEnabled;
    @XmlAttribute(name = "fadeOffset", namespace = "http://schemas.android.com/apk/res/android")
    protected String fadeOffset;
    @XmlAttribute(name = "gestureColor", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureColor;
    @XmlAttribute(name = "gestureStrokeAngleThreshold", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureStrokeAngleThreshold;
    @XmlAttribute(name = "gestureStrokeLengthThreshold", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureStrokeLengthThreshold;
    @XmlAttribute(name = "gestureStrokeSquarenessThreshold", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureStrokeSquarenessThreshold;
    @XmlAttribute(name = "gestureStrokeType", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureStrokeType;
    @XmlAttribute(name = "gestureStrokeWidth", namespace = "http://schemas.android.com/apk/res/android")
    protected String gestureStrokeWidth;
    @XmlAttribute(name = "orientation", namespace = "http://schemas.android.com/apk/res/android")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String orientation;
    @XmlAttribute(name = "uncertainGestureColor", namespace = "http://schemas.android.com/apk/res/android")
    protected String uncertainGestureColor;

    /**
     * Gets the value of the eventsInterceptionEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventsInterceptionEnabled() {
        return eventsInterceptionEnabled;
    }

    /**
     * Sets the value of the eventsInterceptionEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventsInterceptionEnabled(String value) {
        this.eventsInterceptionEnabled = value;
    }

    /**
     * Gets the value of the fadeDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFadeDuration() {
        return fadeDuration;
    }

    /**
     * Sets the value of the fadeDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFadeDuration(String value) {
        this.fadeDuration = value;
    }

    /**
     * Gets the value of the fadeEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFadeEnabled() {
        return fadeEnabled;
    }

    /**
     * Sets the value of the fadeEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFadeEnabled(String value) {
        this.fadeEnabled = value;
    }

    /**
     * Gets the value of the fadeOffset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFadeOffset() {
        return fadeOffset;
    }

    /**
     * Sets the value of the fadeOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFadeOffset(String value) {
        this.fadeOffset = value;
    }

    /**
     * Gets the value of the gestureColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureColor() {
        return gestureColor;
    }

    /**
     * Sets the value of the gestureColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureColor(String value) {
        this.gestureColor = value;
    }

    /**
     * Gets the value of the gestureStrokeAngleThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureStrokeAngleThreshold() {
        return gestureStrokeAngleThreshold;
    }

    /**
     * Sets the value of the gestureStrokeAngleThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureStrokeAngleThreshold(String value) {
        this.gestureStrokeAngleThreshold = value;
    }

    /**
     * Gets the value of the gestureStrokeLengthThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureStrokeLengthThreshold() {
        return gestureStrokeLengthThreshold;
    }

    /**
     * Sets the value of the gestureStrokeLengthThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureStrokeLengthThreshold(String value) {
        this.gestureStrokeLengthThreshold = value;
    }

    /**
     * Gets the value of the gestureStrokeSquarenessThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureStrokeSquarenessThreshold() {
        return gestureStrokeSquarenessThreshold;
    }

    /**
     * Sets the value of the gestureStrokeSquarenessThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureStrokeSquarenessThreshold(String value) {
        this.gestureStrokeSquarenessThreshold = value;
    }

    /**
     * Gets the value of the gestureStrokeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureStrokeType() {
        return gestureStrokeType;
    }

    /**
     * Sets the value of the gestureStrokeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureStrokeType(String value) {
        this.gestureStrokeType = value;
    }

    /**
     * Gets the value of the gestureStrokeWidth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGestureStrokeWidth() {
        return gestureStrokeWidth;
    }

    /**
     * Sets the value of the gestureStrokeWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGestureStrokeWidth(String value) {
        this.gestureStrokeWidth = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrientation(String value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the uncertainGestureColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUncertainGestureColor() {
        return uncertainGestureColor;
    }

    /**
     * Sets the value of the uncertainGestureColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUncertainGestureColor(String value) {
        this.uncertainGestureColor = value;
    }

}
