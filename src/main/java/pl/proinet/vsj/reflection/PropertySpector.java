package pl.proinet.vsj.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.regex.Pattern;

public class PropertySpector {
    
    Class<?> beanClass;
    Class<?> targetClass=null;
    String property;
    AccessibleObject annotationTarget;
    
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?> getTargetClass() throws NoSuchFieldException {
        lazyResolveTarget();
        if( noSuchMethod) throw new NoSuchFieldException("No method for property" + property);
        return targetClass;
    }

    static final Pattern SIGTARGET = Pattern.compile("^L.+\\<L(.+);\\>");

    
    public Optional<Type> getTargetType() {
        lazyResolveTarget();
        if( annotationTarget == null ) return Optional.empty();
        return Optional.ofNullable(getReturnType(annotationTarget));
    }

    public Optional<Type[]> getTargetTypeArguments() {
        return getTargetType().map( tt -> tt instanceof ParameterizedType ? ((ParameterizedType) tt).getActualTypeArguments() : null);
    }

    public static Type getReturnType(AccessibleObject m){
        if( m instanceof java.lang.reflect.Field) {
            Field f = (Field)m;
            return f.getGenericType();
        } else if(m instanceof java.lang.reflect.Method){
            Method mm = (Method)m;
            return mm.getGenericReturnType();
        }
        return null;
    }

    
    public Optional<Class<?>> getTargetClassOptional() {
        lazyResolveTarget();
        return Optional.ofNullable(targetClass);
    }

    public String getProperty() {
        return property;
    }

    public AccessibleObject getPropertyType() throws NoSuchFieldException {
        lazyResolveTarget();
        if( noSuchMethod) throw new NoSuchFieldException("No method for property" + property);
        return annotationTarget;
    }

    public Optional<AccessibleObject> getPropertyTypeOptional() {
        lazyResolveTarget();
        return Optional.ofNullable(annotationTarget);
    }

    public PropertySpector(Class<?> beanClass, String property)  {
        this.beanClass = beanClass;
        this.property = property;
    }

    boolean targetResolved = false;
    boolean noSuchMethod = false;
    private void lazyResolveTarget() {
        if( targetResolved) return;
        resolveTarget();
    }


    private void resolveTarget() {
        String[] names = property.split("\\.");
        Class<?> currentTarget  = beanClass;
        targetResolved = true;
        for( String n: names) {            
            try {
                Field f = currentTarget.getField(n);
                annotationTarget = f;
                currentTarget = f.getType();
            } catch (NoSuchFieldException|SecurityException x) {
                try{
                    Field f = currentTarget.getDeclaredField(n);
                    annotationTarget = f;
                    currentTarget = f.getType();
                } catch (NoSuchFieldException|SecurityException xx) {
                    try {
                        Method m = beanClass.getMethod(toGetter(n));
                        annotationTarget = m;
                        currentTarget = m.getReturnType();
                    } catch (NoSuchMethodException|SecurityException xxx) {
                        noSuchMethod = true;
                        annotationTarget = null;
                        targetClass = null;
                        return;
                    }   
                }
            }
            
        }
        targetClass = currentTarget;
    }

    public <T extends Annotation> Optional<T> targetAnnotation(Class<T> annotation) {
        return Optional.ofNullable(annotationTarget.getAnnotation(annotation));
    }


    public static String toGetter(String name) {
        return "get" + name.substring(0,1).toUpperCase() + name.substring(1);
    }

    public static String toSetter(String name) {
        return "set" + name.substring(0,1).toUpperCase() + name.substring(1);
    }


}
