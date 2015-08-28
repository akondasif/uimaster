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
 * <p>Java class for ViewAnimator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ViewAnimator">
 *   &lt;complexContent>
 *     &lt;extension base="{}FrameLayout">
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}animateFirstView"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}inAnimation"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}outAnimation"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewAnimator")
@XmlSeeAlso({
    ViewFlipper.class,
    ViewSwitcher.class
})
public class ViewAnimator
    extends FrameLayout
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "animateFirstView", namespace = "http://schemas.android.com/apk/res/android")
    protected String animateFirstView;
    @XmlAttribute(name = "inAnimation", namespace = "http://schemas.android.com/apk/res/android")
    protected String inAnimation;
    @XmlAttribute(name = "outAnimation", namespace = "http://schemas.android.com/apk/res/android")
    protected String outAnimation;

    /**
     * Gets the value of the animateFirstView property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimateFirstView() {
        return animateFirstView;
    }

    /**
     * Sets the value of the animateFirstView property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimateFirstView(String value) {
        this.animateFirstView = value;
    }

    /**
     * Gets the value of the inAnimation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInAnimation() {
        return inAnimation;
    }

    /**
     * Sets the value of the inAnimation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInAnimation(String value) {
        this.inAnimation = value;
    }

    /**
     * Gets the value of the outAnimation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutAnimation() {
        return outAnimation;
    }

    /**
     * Sets the value of the outAnimation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutAnimation(String value) {
        this.outAnimation = value;
    }

}
