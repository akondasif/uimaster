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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FrameLayout complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FrameLayout">
 *   &lt;complexContent>
 *     &lt;extension base="{}ViewGroup">
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}foreground"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}foregroundGravity"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}measureAllChildren"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrameLayout")
@XmlSeeAlso({
    TimePicker.class,
    TabHost.class,
    ScrollView.class,
    ViewAnimator.class,
    HorizontalScrollView.class,
    DatePicker.class,
    CalendarView.class,
    GestureOverlayView.class,
    AppWidgetHostView.class,
    MediaController.class
})
public class FrameLayout
    extends ViewGroup
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "foreground", namespace = "http://schemas.android.com/apk/res/android")
    protected String foreground;
    @XmlAttribute(name = "foregroundGravity", namespace = "http://schemas.android.com/apk/res/android")
    protected String foregroundGravity;
    @XmlAttribute(name = "measureAllChildren", namespace = "http://schemas.android.com/apk/res/android")
    protected String measureAllChildren;

    /**
     * Gets the value of the foreground property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeground() {
        return foreground;
    }

    /**
     * Sets the value of the foreground property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeground(String value) {
        this.foreground = value;
    }

    /**
     * Gets the value of the foregroundGravity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForegroundGravity() {
        return foregroundGravity;
    }

    /**
     * Sets the value of the foregroundGravity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForegroundGravity(String value) {
        this.foregroundGravity = value;
    }

    /**
     * Gets the value of the measureAllChildren property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeasureAllChildren() {
        return measureAllChildren;
    }

    /**
     * Sets the value of the measureAllChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeasureAllChildren(String value) {
        this.measureAllChildren = value;
    }

}
