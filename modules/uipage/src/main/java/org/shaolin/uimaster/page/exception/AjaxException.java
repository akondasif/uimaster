package org.shaolin.uimaster.page.exception;

import org.shaolin.bmdp.i18n.Localizer;

/**
 * Exception for Ajax
 *
 */
public class AjaxException extends UIPageException
{
    /**
     * Constructs a AjaxException with a given exception reason
     *
     * @param aReason
     */
    public AjaxException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a AjaxException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public AjaxException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a AjaxException given a exception reason, a nested Throwable
     * object.
     *
     * @param aReason
     * @param aThrowable
     */
    public AjaxException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a AjaxException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public AjaxException(String aReason, Throwable aThrowable,
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a AjaxException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public AjaxException(String aReason, Throwable aThrowable,
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

    private static final long serialVersionUID = 6637940630141909726L;
}

