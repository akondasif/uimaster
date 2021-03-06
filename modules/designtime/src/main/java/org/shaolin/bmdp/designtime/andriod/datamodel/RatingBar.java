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
 * <p>Java class for RatingBar complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RatingBar">
 *   &lt;complexContent>
 *     &lt;extension base="{}AbsSeekBar">
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}isIndicator"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}numStars"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}rating"/>
 *       &lt;attribute ref="{http://schemas.android.com/apk/res/android}stepSize"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RatingBar")
@XmlRootElement(name = "RatingBar")
public class RatingBar
    extends AbsSeekBar
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "isIndicator", namespace = "http://schemas.android.com/apk/res/android")
    protected String isIndicator;
    @XmlAttribute(name = "numStars", namespace = "http://schemas.android.com/apk/res/android")
    protected String numStars;
    @XmlAttribute(name = "rating", namespace = "http://schemas.android.com/apk/res/android")
    protected String rating;
    @XmlAttribute(name = "stepSize", namespace = "http://schemas.android.com/apk/res/android")
    protected String stepSize;

    /**
     * Gets the value of the isIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsIndicator() {
        return isIndicator;
    }

    /**
     * Sets the value of the isIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsIndicator(String value) {
        this.isIndicator = value;
    }

    /**
     * Gets the value of the numStars property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumStars() {
        return numStars;
    }

    /**
     * Sets the value of the numStars property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumStars(String value) {
        this.numStars = value;
    }

    /**
     * Gets the value of the rating property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRating(String value) {
        this.rating = value;
    }

    /**
     * Gets the value of the stepSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStepSize() {
        return stepSize;
    }

    /**
     * Sets the value of the stepSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStepSize(String value) {
        this.stepSize = value;
    }

}
