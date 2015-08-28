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


/**
 * <p>Java class for Gallery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Gallery">
 *   &lt;complexContent>
 *     &lt;extension base="{}AbsSpinner">
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}animationDuration"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}gravity"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}spacing"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}unselectedAlpha"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Gallery")
@XmlRootElement(name = "Gallery")
public class Gallery
    extends AbsSpinner
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "animationDuration", namespace = "http://schemas.android.com/apk/res/android")
    protected String animationDuration;
    @XmlAttribute(name = "gravity", namespace = "http://schemas.android.com/apk/res/android")
    protected String gravity;
    @XmlAttribute(name = "spacing", namespace = "http://schemas.android.com/apk/res/android")
    protected String spacing;
    @XmlAttribute(name = "unselectedAlpha", namespace = "http://schemas.android.com/apk/res/android")
    protected String unselectedAlpha;

    /**
     * Gets the value of the animationDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimationDuration() {
        return animationDuration;
    }

    /**
     * Sets the value of the animationDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimationDuration(String value) {
        this.animationDuration = value;
    }

    /**
     * Gets the value of the gravity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGravity() {
        return gravity;
    }

    /**
     * Sets the value of the gravity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGravity(String value) {
        this.gravity = value;
    }

    /**
     * Gets the value of the spacing property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpacing() {
        return spacing;
    }

    /**
     * Sets the value of the spacing property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpacing(String value) {
        this.spacing = value;
    }

    /**
     * Gets the value of the unselectedAlpha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnselectedAlpha() {
        return unselectedAlpha;
    }

    /**
     * Sets the value of the unselectedAlpha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnselectedAlpha(String value) {
        this.unselectedAlpha = value;
    }

}
