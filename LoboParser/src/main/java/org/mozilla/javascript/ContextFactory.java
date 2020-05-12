/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// API class

package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Factory class that Rhino runtime uses to create new {@link org.mozilla.javascript.Context}
 * instances.  A ContextFactory can also notify listeners
 * about context creation and release.
 * <p>
 * When the Rhino runtime needs to create new {@link org.mozilla.javascript.Context} instance during
 * execution of {@link org.mozilla.javascript.Context#enter()} or {@link org.mozilla.javascript.Context}, it will call
 * {@link #makeContext()} of the current global ContextFactory.
 * See {@link #getGlobal()} and {@link #initGlobal(ContextFactory)}.
 * <p>
 * It is also possible to use explicit ContextFactory instances for Context
 * creation. This is useful to have a set of independent Rhino runtime
 * instances under single JVM. See {@link #call(ContextAction)}.
 * <p>
 * The following example demonstrates Context customization to terminate
 * scripts running more then 10 seconds and to provide better compatibility
 * with JavaScript code using MSIE-specific features.
 * <pre>
 * import org.mozilla.javascript.*;
 *
 * class MyFactory extends ContextFactory
 * {
 *
 *     // Custom {@link org.mozilla.javascript.Context} to store execution time.
 *     private static class MyContext extends Context
 *     {
 *         long startTime;
 *     }
 *
 *     static {
 *         // Initialize GlobalFactory with custom factory
 *         ContextFactory.initGlobal(new MyFactory());
 *     }
 *
 *     // Override {@link #makeContext()}
 *     protected Context makeContext()
 *     {
 *         MyContext cx = new MyContext();
 *         // Make Rhino runtime to call observeInstructionCount
 *         // each 10000 bytecode instructions
 *         cx.setInstructionObserverThreshold(10000);
 *         return cx;
 *     }
 *
 *     // Override {@link #hasFeature(Context, int)}
 *     public boolean hasFeature(Context cx, int featureIndex)
 *     {
 *         // Turn on maximum compatibility with MSIE scripts
 *         switch (featureIndex) {
 *             case {@link org.mozilla.javascript.Context#FEATURE_NON_ECMA_GET_YEAR}:
 *                 return true;
 *
 *             case {@link org.mozilla.javascript.Context#FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME}:
 *                 return true;
 *
 *             case {@link org.mozilla.javascript.Context#FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER}:
 *                 return true;
 *
 *             case {@link org.mozilla.javascript.Context#FEATURE_PARENT_PROTO_PROPERTIES}:
 *                 return false;
 *         }
 *         return super.hasFeature(cx, featureIndex);
 *     }
 *
 *     // Override {@link #observeInstructionCount(Context, int)}
 *     protected void observeInstructionCount(Context cx, int instructionCount)
 *     {
 *         MyContext mcx = (MyContext)cx;
 *         long currentTime = System.currentTimeMillis();
 *         if (currentTime - mcx.startTime &gt; 10*1000) {
 *             // More then 10 seconds from Context creation time:
 *             // it is time to stop the script.
 *             // Throw Error instance to ensure that script will never
 *             // get control back through catch or finally.
 *             throw new Error();
 *         }
 *     }
 *
 *     // Override {@link #doTopCall(Callable,
 *                               Context, Scriptable,
 *                               Scriptable, Object[])}
 *     protected Object doTopCall(Callable callable,
 *                                Context cx, Scriptable scope,
 *                                Scriptable thisObj, Object[] args)
 *     {
 *         MyContext mcx = (MyContext)cx;
 *         mcx.startTime = System.currentTimeMillis();
 *
 *         return super.doTopCall(callable, cx, scope, thisObj, args);
 *     }
 *
 * }
 * </pre>
 *
 * @author utente
 * @version $Id: $Id
 */
public class ContextFactory
{
    private static volatile boolean hasCustomGlobal;
    private static ContextFactory global = new ContextFactory();

    private volatile boolean sealed;

    private final Object listenersLock = new Object();
    private volatile Object listeners;
    private boolean disabledListening;
    private ClassLoader applicationClassLoader;

    /**
     * Listener of {@link Context} creation and release events.
     */
    public interface Listener
    {
        /**
         * Notify about newly created {@link Context} object.
         * @param cx a Context to associate with the thread if possible
         */
        public void contextCreated(Context cx);

        /**
         * Notify that the specified {@link Context} instance is no longer
         * associated with the current thread.
         * @param cx a Context to associate with the thread if possible
         */
        public void contextReleased(Context cx);
    }

    /**
     * Get global ContextFactory.
     *
     * @see #hasExplicitGlobal()
     * @see #initGlobal(ContextFactory)
     * @return a {@link org.mozilla.javascript.ContextFactory} object.
     */
    public static ContextFactory getGlobal()
    {
        return global;
    }

    /**
     * Check if global factory was set.
     * Return true to indicate that {@link #initGlobal(ContextFactory)} was
     * already called and false to indicate that the global factory was not
     * explicitly set.
     *
     * @see #getGlobal()
     * @see #initGlobal(ContextFactory)
     * @return a boolean.
     */
    public static boolean hasExplicitGlobal()
    {
        return hasCustomGlobal;
    }

    /**
     * Set global ContextFactory.
     * The method can only be called once.
     *
     * @see #getGlobal()
     * @see #hasExplicitGlobal()
     * @param factory a {@link org.mozilla.javascript.ContextFactory} object.
     */
    public synchronized static void initGlobal(ContextFactory factory)
    {
        if (factory == null) {
            throw new IllegalArgumentException();
        }
        if (hasCustomGlobal) {
            throw new IllegalStateException();
        }
        hasCustomGlobal = true;
        global = factory;
    }

    public interface GlobalSetter {
        public void setContextFactoryGlobal(ContextFactory factory);
        public ContextFactory getContextFactoryGlobal();
    }

    /**
     * <p>getGlobalSetter.</p>
     *
     * @return a {@link org.mozilla.javascript.ContextFactory.GlobalSetter} object.
     */
    public synchronized static GlobalSetter getGlobalSetter() {
        if (hasCustomGlobal) {
            throw new IllegalStateException();
        }
        hasCustomGlobal = true;
        class GlobalSetterImpl implements GlobalSetter {
            @Override
            public void setContextFactoryGlobal(ContextFactory factory) {
                global = factory == null ? new ContextFactory() : factory;
            }
            @Override
            public ContextFactory getContextFactoryGlobal() {
                return global;
            }
        }
        return new GlobalSetterImpl();
    }

    /**
     * Create new {@link org.mozilla.javascript.Context} instance to be associated with the current
     * thread.
     * This is a callback method used by Rhino to create {@link org.mozilla.javascript.Context}
     * instance when it is necessary to associate one with the current
     * execution thread. <tt>makeContext()</tt> is allowed to call
     * {@link org.mozilla.javascript.Context#seal(Object)} on the result to prevent
     * {@link org.mozilla.javascript.Context} changes by hostile scripts or applets.
     *
     * @return a {@link org.mozilla.javascript.Context} object.
     */
    protected Context makeContext()
    {
        return new Context(this);
    }

    /**
     * Implementation of {@link org.mozilla.javascript.Context#hasFeature(int featureIndex)}.
     * This can be used to customize {@link org.mozilla.javascript.Context} without introducing
     * additional subclasses.
     *
     * @param cx a {@link org.mozilla.javascript.Context} object.
     * @param featureIndex a int.
     * @return a boolean.
     */
    protected boolean hasFeature(Context cx, int featureIndex)
    {
        int version;
        switch (featureIndex) {
          case Context.FEATURE_NON_ECMA_GET_YEAR:
           /*
            * During the great date rewrite of 1.3, we tried to track the
            * evolving ECMA standard, which then had a definition of
            * getYear which always subtracted 1900.  Which we
            * implemented, not realizing that it was incompatible with
            * the old behavior...  now, rather than thrash the behavior
            * yet again, we've decided to leave it with the - 1900
            * behavior and point people to the getFullYear method.  But
            * we try to protect existing scripts that have specified a
            * version...
            */
            version = cx.getLanguageVersion();
            return (version == Context.VERSION_1_0
                    || version == Context.VERSION_1_1
                    || version == Context.VERSION_1_2);

          case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
            return false;

          case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
            return true;

          case Context.FEATURE_TO_STRING_AS_SOURCE:
            version = cx.getLanguageVersion();
            return version == Context.VERSION_1_2;

          case Context.FEATURE_PARENT_PROTO_PROPERTIES:
            return true;

          case Context.FEATURE_E4X:
            version = cx.getLanguageVersion();
            return (version == Context.VERSION_DEFAULT
                    || version >= Context.VERSION_1_6);

          case Context.FEATURE_DYNAMIC_SCOPE:
            return false;

          case Context.FEATURE_STRICT_VARS:
            return false;

          case Context.FEATURE_STRICT_EVAL:
            return false;

          case Context.FEATURE_LOCATION_INFORMATION_IN_ERROR:
            return false;

          case Context.FEATURE_STRICT_MODE:
            return false;

          case Context.FEATURE_WARNING_AS_ERROR:
            return false;

          case Context.FEATURE_ENHANCED_JAVA_ACCESS:
            return false;

          case Context.FEATURE_V8_EXTENSIONS:
            return true;

          case Context.FEATURE_OLD_UNDEF_NULL_THIS:
              return cx.getLanguageVersion() <= Context.VERSION_1_7;

          case Context.FEATURE_ENUMERATE_IDS_FIRST:
              return cx.getLanguageVersion() >= Context.VERSION_ES6;

          case Context.FEATURE_THREAD_SAFE_OBJECTS:
              return false;

          case Context.FEATURE_INTEGER_WITHOUT_DECIMAL_PLACE:
              return false;

          case Context.FEATURE_LITTLE_ENDIAN:
              return false;

          case Context.FEATURE_ENABLE_XML_SECURE_PARSING:
              return true;
        }
        // It is a bug to call the method with unknown featureIndex
        throw new IllegalArgumentException(String.valueOf(featureIndex));
    }

    private boolean isDom3Present() {
        Class<?> nodeClass = Kit.classOrNull("org.w3c.dom.Node");
        if (nodeClass == null) return false;
        // Check to see whether DOM3 is present; use a new method defined in
        // DOM3 that is vital to our implementation
        try {
            nodeClass.getMethod("getUserData", new Class<?>[] { String.class });
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Provides a default
     * {@link org.mozilla.javascript.xml.XMLLib.Factory XMLLib.Factory}
     * to be used by the Context instances produced by this
     * factory. See {@link org.mozilla.javascript.Context#getE4xImplementationFactory} for details.
     *
     * May return null, in which case E4X functionality is not supported in
     * Rhino.
     *
     * The default implementation now prefers the DOM3 E4X implementation.
     *
     * @return a {@link org.mozilla.javascript.xml.XMLLib.Factory} object.
     */
    protected org.mozilla.javascript.xml.XMLLib.Factory
        getE4xImplementationFactory()
    {
        // Must provide default implementation, rather than abstract method,
        // so that past implementors of ContextFactory do not fail at runtime
        // upon invocation of this method.
        // Note that the default implementation returns null if we
        // neither have XMLBeans nor a DOM3 implementation present.

        if (isDom3Present()) {
            return org.mozilla.javascript.xml.XMLLib.Factory.create(
                "org.mozilla.javascript.xmlimpl.XMLLibImpl"
            );
        }
        return null;
    }


    /**
     * Create class loader for generated classes.
     * This method creates an instance of the default implementation
     * of {@link org.mozilla.javascript.GeneratedClassLoader}. Rhino uses this interface to load
     * generated JVM classes when no {@link org.mozilla.javascript.SecurityController}
     * is installed.
     * Application can override the method to provide custom class loading.
     *
     * @param parent a {@link java.lang.ClassLoader} object.
     * @return a {@link org.mozilla.javascript.GeneratedClassLoader} object.
     */
    protected GeneratedClassLoader createClassLoader(final ClassLoader parent)
    {
        return AccessController.doPrivileged(new PrivilegedAction<DefiningClassLoader>() {
            @Override
            public DefiningClassLoader run(){
                return new DefiningClassLoader(parent);
            }
        });
    }

    /**
     * Get ClassLoader to use when searching for Java classes.
     * Unless it was explicitly initialized with
     * {@link #initApplicationClassLoader(ClassLoader)} the method returns
     * null to indicate that Thread.getContextClassLoader() should be used.
     *
     * @return a {@link java.lang.ClassLoader} object.
     */
    public final ClassLoader getApplicationClassLoader()
    {
        return applicationClassLoader;
    }

    /**
     * Set explicit class loader to use when searching for Java classes.
     *
     * @see #getApplicationClassLoader()
     * @param loader a {@link java.lang.ClassLoader} object.
     */
    public final void initApplicationClassLoader(ClassLoader loader)
    {
        if (loader == null)
            throw new IllegalArgumentException("loader is null");
        if (!Kit.testIfCanLoadRhinoClasses(loader))
            throw new IllegalArgumentException(
                "Loader can not resolve Rhino classes");

        if (this.applicationClassLoader != null)
            throw new IllegalStateException(
                "applicationClassLoader can only be set once");
        checkNotSealed();

        this.applicationClassLoader = loader;
    }

    /**
     * Execute top call to script or function.
     * When the runtime is about to execute a script or function that will
     * create the first stack frame with scriptable code, it calls this method
     * to perform the real call. In this way execution of any script
     * happens inside this function.
     *
     * @param callable a {@link org.mozilla.javascript.Callable} object.
     * @param cx a {@link org.mozilla.javascript.Context} object.
     * @param scope a {@link org.mozilla.javascript.Scriptable} object.
     * @param thisObj a {@link org.mozilla.javascript.Scriptable} object.
     * @param args an array of {@link java.lang.Object} objects.
     * @return a {@link java.lang.Object} object.
     */
    protected Object doTopCall(Callable callable,
                               Context cx, Scriptable scope,
                               Scriptable thisObj, Object[] args)
    {
        Object result = callable.call(cx, scope, thisObj, args);
        return result instanceof ConsString ? result.toString() : result;
    }

    /**
     * Implementation of
     * {@link org.mozilla.javascript.Context#observeInstructionCount(int instructionCount)}.
     * This can be used to customize {@link org.mozilla.javascript.Context} without introducing
     * additional subclasses.
     *
     * @param cx a {@link org.mozilla.javascript.Context} object.
     * @param instructionCount a int.
     */
    protected void observeInstructionCount(Context cx, int instructionCount) {
    }

    /**
     * <p>onContextCreated.</p>
     *
     * @param cx a {@link org.mozilla.javascript.Context} object.
     */
    protected void onContextCreated(Context cx)
    {
        Object listeners = this.listeners;
        for (int i = 0; ; ++i) {
            Listener l = (Listener)Kit.getListener(listeners, i);
            if (l == null)
                break;
            l.contextCreated(cx);
        }
    }

    /**
     * <p>onContextReleased.</p>
     *
     * @param cx a {@link org.mozilla.javascript.Context} object.
     */
    protected void onContextReleased(Context cx)
    {
        Object listeners = this.listeners;
        for (int i = 0; ; ++i) {
            Listener l = (Listener)Kit.getListener(listeners, i);
            if (l == null)
                break;
            l.contextReleased(cx);
        }
    }

    /**
     * <p>addListener.</p>
     *
     * @param listener a {@link org.mozilla.javascript.ContextFactory.Listener} object.
     */
    public final void addListener(Listener listener)
    {
        checkNotSealed();
        synchronized (listenersLock) {
            if (disabledListening) {
                throw new IllegalStateException();
            }
            listeners = Kit.addListener(listeners, listener);
        }
    }

    /**
     * <p>removeListener.</p>
     *
     * @param listener a {@link org.mozilla.javascript.ContextFactory.Listener} object.
     */
    public final void removeListener(Listener listener)
    {
        checkNotSealed();
        synchronized (listenersLock) {
            if (disabledListening) {
                throw new IllegalStateException();
            }
            listeners = Kit.removeListener(listeners, listener);
        }
    }

    /**
     * The method is used only to implement
     * Context.disableStaticContextListening()
     */
    final void disableContextListening()
    {
        checkNotSealed();
        synchronized (listenersLock) {
            disabledListening = true;
            listeners = null;
        }
    }

    /**
     * Checks if this is a sealed ContextFactory.
     *
     * @see #seal()
     * @return a boolean.
     */
    public final boolean isSealed()
    {
        return sealed;
    }

    /**
     * Seal this ContextFactory so any attempt to modify it like to add or
     * remove its listeners will throw an exception.
     *
     * @see #isSealed()
     */
    public final void seal()
    {
        checkNotSealed();
        sealed = true;
    }

    /**
     * <p>checkNotSealed.</p>
     */
    protected final void checkNotSealed()
    {
        if (sealed) throw new IllegalStateException();
    }

    /**
     * Call {@link org.mozilla.javascript.ContextAction#run(Context cx)}
     * using the {@link org.mozilla.javascript.Context} instance associated with the current thread.
     * If no Context is associated with the thread, then
     * {@link #makeContext()} will be called to construct
     * new Context instance. The instance will be temporary associated
     * with the thread during call to {@link org.mozilla.javascript.ContextAction#run(Context)}.
     *
     * @see ContextFactory#call(ContextAction)
     * @see Context#call(ContextFactory factory, Callable callable,
     *                   Scriptable scope, Scriptable thisObj,
     *                   Object[] args)
     * @param action a {@link org.mozilla.javascript.ContextAction} object.
     * @param <T> a T object.
     * @return a T object.
     */
    public final <T> T call(ContextAction<T> action)
    {
        return Context.call(this, action);
    }

    /**
     * Get a context associated with the current thread, creating one if need
     * be. The Context stores the execution state of the JavaScript engine, so
     * it is required that the context be entered before execution may begin.
     * Once a thread has entered a Context, then getCurrentContext() may be
     * called to find the context that is associated with the current thread.
     * <p>
     * Calling enterContext() will return either the Context
     * currently associated with the thread, or will create a new context and
     * associate it with the current thread. Each call to
     * enterContext() must have a matching call to
     * {@link org.mozilla.javascript.Context#exit()}.
     * <pre>
     *      Context cx = contextFactory.enterContext();
     *      try {
     *          ...
     *          cx.evaluateString(...);
     *      } finally {
     *          Context.exit();
     *      }
     * </pre>
     * Instead of using <tt>enterContext()</tt>, <tt>exit()</tt> pair consider
     * using {@link #call(ContextAction)} which guarantees proper association
     * of Context instances with the current thread.
     * With this method the above example becomes:
     * <pre>
     *      ContextFactory.call(new ContextAction() {
     *          public Object run(Context cx) {
     *              ...
     *              cx.evaluateString(...);
     *              return null;
     *          }
     *      });
     * </pre>
     *
     * @return a Context associated with the current thread
     * @see Context#getCurrentContext()
     * @see Context#exit()
     * @see #call(ContextAction)
     */
    public Context enterContext()
    {
        return enterContext(null);
    }

    /**
     * <p>enter.</p>
     *
     * @deprecated use {@link #enterContext()} instead
     * @return a Context associated with the current thread
     */
    @Deprecated
    public final Context enter()
    {
        return enterContext(null);
    }

    /**
     * <p>exit.</p>
     *
     * @deprecated Use {@link org.mozilla.javascript.Context#exit()} instead.
     */
    @Deprecated
    public final void exit()
    {
        Context.exit();
    }

    /**
     * Get a Context associated with the current thread, using the given
     * Context if need be.
     * <p>
     * The same as enterContext() except that cx
     * is associated with the current thread and returned if the current thread
     * has no associated context and cx is not associated with any
     * other thread.
     *
     * @param cx a Context to associate with the thread if possible
     * @return a Context associated with the current thread
     * @see #enterContext()
     * @see #call(ContextAction)
     * @throws java.lang.IllegalStateException if cx is already associated
     * with a different thread
     */
    public final Context enterContext(Context cx)
    {
        return Context.enter(cx, this);
    }
}
